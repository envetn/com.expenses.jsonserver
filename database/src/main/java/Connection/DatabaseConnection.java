package Connection;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jsonserver.common.Utils.Utilities;
import jsonserver.common.datatype.*;
import org.apache.log4j.Logger;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.IntStream;

import jsonserver.common.view.DbView;
import jsonserver.common.view.DeleteRequest;
import jsonserver.common.view.Expense.ExpensePutRequest;
import jsonserver.common.view.GetRequest;
import jsonserver.common.view.Temperature.TemperaturePutRequest;
import jsonserver.common.view.Request;

import static jsonserver.common.datatype.RequestId.ValidRequestIdEnum.EXPENSES;

/**
 * Created by olof on 2016-07-09.
 */
public class DatabaseConnection implements DbView
{
    //TODO: This Connection.DatabaseConnection could be an OSGI services
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class);
    public final static Integer SQL_FAILED = 0;

    private Connection connect;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private static Date myDate;

    private static Set<ExpenseUser> myExistingUsers = new HashSet<>();

    // http://www.vogella.com/tutorials/MySQLJava/article.html
    public DatabaseConnection() throws ClassNotFoundException
    {
        Class.forName("com.mysql.jdbc.Driver");
        //TODO keep track of existing tables in the DB
    }

    @Override
    public void openConnection()
    {
        try
        {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/test?user=root&password=root&amp;useUnicode=true&characterEncoding=utf8");
            statement = connect.createStatement();
        }
        catch (SQLException e)
        {
            logger.info("Connection.DatabaseConnection . SQLException " + e.toString());
        }
    }

    @Override
    public void closeConnection()
    {
        try
        {
            connect.close();
            if (resultSet != null)
            {
                resultSet.close();
            }

            if (preparedStatement != null)
            {
                preparedStatement.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            logger.info("Failed to close database connection: " + e.toString());
        }
    }

    @Override
    public UserContainer createUserContainer(Request request) throws SQLException
    {
        openConnection();
        List<Threshold> thresholds = readThresholdValue(request);
        List<Expenses> expenses = readExpensesValue(request);
        List<Temperature> temperature = readTemperatureValue(request);

        closeConnection();
        return new UserContainer(request, expenses, thresholds, temperature);
    }



    @Override
    public ImmutableList<ExpenseUser> getCachedUsers()
    {
        return ImmutableList.copyOf(myExistingUsers);
    }

    @Override
    public ImmutableList<ExpenseUser> loadUsers() throws SQLException
    {
        String sqlString = "SELECT * FROM expenseuser";
        preparedStatement = connect.prepareStatement(sqlString);
        logger.info(sqlString);
        resultSet = preparedStatement.executeQuery();
        List<ExpenseUser> loadedUsers = new ArrayList<>();
        while (resultSet.next())
        {
            String username = resultSet.getString("username");
            String passwd = resultSet.getString("passwd");
            int id = resultSet.getInt("id");
            ExpenseUser user = new ExpenseUser(id + "", username, passwd);
            myExistingUsers.add(user);
            loadedUsers.add(user);
        }
        return ImmutableList.copyOf(loadedUsers);
    }

    @Override
    public JsonObject readFromContainer(UserContainer container)
    {
        Request request = container.getRequest();
        RequestId id = request.getId();

        if(id.equals(EXPENSES.getId()))
        {
            ExpensesDbConnection expensesDbConnection = new ExpensesDbConnection((GetRequest)request, container.getExpenses(), container.getThresholds());
            expensesDbConnection.createExpensesResponse();
        }


    }

    public int createUser(Request createUserRequest) throws SQLException
    {
        String database = getDatabaseTable(createUserRequest);
        ExpenseUser user = createUserRequest.getUser();

        String sql = "INSERT INTO %s (username, passwd, created) VALUES (?,?,?)";
        preparedStatement = connect.prepareStatement(String.format(sql, database));
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setDate(3, createUserRequest.getRequestDate());

        logger.info("Statement: " + preparedStatement.toString());
        return preparedStatement.executeUpdate();
    }

    public int removeData(DeleteRequest deleteRequest) throws SQLException
    {
        String database = getDatabaseTable(deleteRequest);
        ExpenseUser user = (deleteRequest).getUser();
        List<String> idsToRemove = deleteRequest.getIdToRemove();
        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM %s WHERE uuid IN (");

        IntStream.range(0, idsToRemove.size())
                .mapToObj(i -> (i >= idsToRemove.size() - 1 ? "?) AND username=?;" : "?,"))
                .forEach(sqlBuilder::append);
        String sql = sqlBuilder.toString();

        preparedStatement = connect.prepareStatement(String.format(sql, database));

        int id = 0;
        for (String idToRemove : idsToRemove)
        {
            String replacedId = idToRemove.replace("'", "");
            preparedStatement.setString(++id, replacedId);
        }
        preparedStatement.setString(++id, user.getUsername());

        logger.info("preparedStatement: " + preparedStatement.toString());
        return preparedStatement.executeUpdate();
    }

    public ResultSet executeGetRequest(GetRequest getRequest) throws SQLException
    {
        String database = getDatabaseTable(getRequest);
        RequestId requestId = getRequest.getId();
        Limit limit = getRequest.getLimit();
        Order order = getRequest.getOrder();

        GetQuery.QueryBuilder queryBuilder = GetQuery.getBuilder()
                .setConnect(connect)
                .setDatabase(database)
                .setLimit(limit)
                .setId(requestId.asString())
                .setOrder(order);

        //Temperature does not have entry with user yet
        if (requestId.equals(EXPENSES.getId()))
        {
            queryBuilder.setUser((getRequest).getUser());
        }
        GetQuery query = queryBuilder.createQuery();
        PreparedStatement statement = query.getStatement();

        logger.info("Statement: " + statement.toString());
        resultSet = statement.executeQuery();

        return resultSet;
    }

    public int insertExpensesValues(ExpensePutRequest request) throws SQLException
    {
        String table = getDatabaseTable(request);

        Content content = request.getContent();
        ExpenseUser user = request.getUser();
        String query = "INSERT INTO test.%s (cost, costType, buyDate, comment, uuid, username) VALUES (?,?,?,?,?,?)";
        String sql = String.format(query, table);
        preparedStatement = connect.prepareStatement(sql);

        preparedStatement.setString(1, content.getCost());
        preparedStatement.setString(2, content.getCostType());
        preparedStatement.setDate(3, request.getRequestDate());
        preparedStatement.setString(4, content.getComment());
        preparedStatement.setString(5, content.getUUID());
        preparedStatement.setString(6, user.getUsername());

        logger.info("Statement: " + preparedStatement.toString());
        int result = preparedStatement.executeUpdate();

        //TODO, if no table with threshold values, what to do?
        updateThresholdValues(request);

        return result;
    }

    public int updateThresholdValues(Request request) throws SQLException
    {
        String query  = "UPDATE " +
                "test.threshold t2, " +
                    "(" +
                       "SELECT SUM(cost) AS totalCost FROM test.expenses WHERE buyDate BETWEEN ? AND ? AND costType = ? AND username = ?" +
                    ")" +
                " t1 "+
           "SET t2.currentCost = t1.totalCost " +
           "WHERE t2.month = ? AND t2.username = ? AND t2.type = ?";


        Content content = ((ExpensePutRequest) request).getContent();
        Date date = content.getbuyDate();
        int monthNumber = Utilities.getMonthNumber(date) + 1;
        Map<String, String> firstAndLastDayOf = Utilities.getFirstAndLastDayOf(monthNumber);

        preparedStatement = connect.prepareStatement(query);

        preparedStatement.setString(1, firstAndLastDayOf.get("first"));
        preparedStatement.setString(2, firstAndLastDayOf.get("last"));
        preparedStatement.setString(3, content.getCostType());
        preparedStatement.setString(4, request.getUser().getUsername());
        preparedStatement.setInt(5, monthNumber);
        preparedStatement.setString(6, request.getUser().getUsername());
        preparedStatement.setString(7, content.getCostType());

        logger.info("Statement: " + preparedStatement.toString());
        int affectedRows = preparedStatement.executeUpdate();
        logger.info("affected rows by statment: " + affectedRows);
        return affectedRows;
    }

    //old
    public JsonArray readthresholdValue(GetRequest request) throws SQLException
    {
        Fetchperiod fetchperiod = request.getLimit().getFetchperiod();

        //evaluate all month threshold
        StringBuilder quierBuilder = new StringBuilder();
        quierBuilder.append("SELECT * FROM test.threshold WHERE username=?");

        if("All".equals(fetchperiod.getPeriod()))
        {
            String query = quierBuilder.toString();

            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1, request.getUser().getUsername());
        }
        else
        {

            quierBuilder.append(" AND month=?");
            String query = quierBuilder.toString();
            String timeStart = fetchperiod.getTimeStart() != null ? fetchperiod.getTimeStart() : fetchperiod.getPeriodToFetch();
            Date date = Date.valueOf(timeStart);

            int monthNumber = Utilities.getMonthNumber(date) + 1;

            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1, request.getUser().getUsername());
            preparedStatement.setInt(2, monthNumber);
        }


        logger.info("Statement: " + preparedStatement.toString());
        resultSet = preparedStatement.executeQuery();

        List<Threshold> thresholds = new ArrayList<>();

        while (resultSet.next())
        {
            int currentValue = resultSet.getInt("currentCost");
            int threshold = resultSet.getInt("threshold");

            String type = resultSet.getString("type");
            int momnth = resultSet.getInt("month");
            thresholds.add(new Threshold(currentValue, threshold, momnth, type));
        }

        JsonArray jsonArray = new JsonArray();

        for (Threshold threshold : thresholds)
        {
            JsonObject object = threshold.createObject();
            jsonArray.add(object);
        }

        return jsonArray;
    }



    private List<Temperature> readTemperatureValue(Request request) throws SQLException
    {
//        String query = "SELECT * FROM test.temperature WHERE username=?";
//
//        preparedStatement = connect.prepareStatement(query);
//        preparedStatement.setString(1, request.getUser().getUsername());
//
//        logger.info("Statement: " + preparedStatement.toString());
//        resultSet = preparedStatement.executeQuery();
//
//        List<Temperature> listOfTemperature= new ArrayList<>();
//
//        while (resultSet.next())
//        {
//            JsonObject object = new JsonObject();
//            String date = resultSet.getString("date");
//            String temperatur = resultSet.getString("temperatur");
//            Date time =  Date.valueOf(resultSet.getString("time"));
//            Temperature temperature = new Temperature(date, temperatur, time);
//
//            listOfTemperature.add(temperature);
//
//
//        }
//        return listOfTemperature;
        return Collections.emptyList();

    }


    //new
    private List<Expenses> readExpensesValue(Request request) throws SQLException
    {
        String query = "SELECT * FROM test.expenses WHERE username=?";

        preparedStatement = connect.prepareStatement(query);
        preparedStatement.setString(1, request.getUser().getUsername());

        logger.info("Statement: " + preparedStatement.toString());
        resultSet = preparedStatement.executeQuery();

        List<Expenses> listOfExpenses= new ArrayList<>();

        while (resultSet.next())
        {
            JsonObject object = new JsonObject();
            String cost = resultSet.getString("cost");
            String costType = resultSet.getString("costType");
            Date buyDate =  Date.valueOf(resultSet.getString("buyDate"));
            String comment =  resultSet.getString("comment");
            String uuid =  resultSet.getString("uuid");

            Expenses expenses = new Expenses(cost, costType, buyDate, comment, uuid);

            listOfExpenses.add(expenses);


        }
        return listOfExpenses;
    }

    //new
    private List<Threshold> readThresholdValue(Request request) throws SQLException
    {
        StringBuilder quierBuilder = new StringBuilder();
        quierBuilder.append("SELECT * FROM test.threshold WHERE username=?");

        String query = quierBuilder.toString();

        preparedStatement = connect.prepareStatement(query);
        preparedStatement.setString(1, request.getUser().getUsername());

        logger.info("Statement: " + preparedStatement.toString());
        resultSet = preparedStatement.executeQuery();

        List<Threshold> thresholds = new ArrayList<>();

        while (resultSet.next())
        {
            int currentValue = resultSet.getInt("currentCost");
            int threshold = resultSet.getInt("threshold");

            String type = resultSet.getString("type");
            int momnth = resultSet.getInt("month");
            thresholds.add(new Threshold(currentValue, threshold, momnth, type));
        }
        return thresholds;
    }

    /**
     * Fetch threshold by Username and month.
     */


    public int insertTemperatureValues(TemperaturePutRequest putRequest) throws SQLException
    {
        Date incomingDate = putRequest.getRequestDate();

        resultSet = statement.executeQuery("SELECT id, date FROM test.temperature ORDER BY id DESC LIMIT 1 ");
        while (resultSet.next())
        {
            myDate = resultSet.getDate("date");
        }

        //TODO: remove toString when they have the same Long myValue, seems to be a big when the put request is created
        if (myDate.toString()
                .equals(incomingDate.toString()))
        {
            appendData(putRequest);
        }
        else
        {
            myDate = incomingDate;
            insertData(putRequest);
        }
        logger.info("Statement: " + preparedStatement.toString());
        return preparedStatement.executeUpdate();
    }

    private void appendData(TemperaturePutRequest request) throws SQLException
    {
        String sql = "UPDATE test.temperature SET time=CONCAT(time,?), temperatur=CONCAT(temperatur,?)  WHERE date=?  LIMIT 1";
        preparedStatement = connect.prepareStatement(sql);

        preparedStatement.setString(1, request.getRequestTime() + "@");
        preparedStatement.setString(2, request.getTemperature() + "@");
        preparedStatement.setDate(3, request.getRequestDate());
    }

    private void insertData(TemperaturePutRequest request) throws SQLException
    {
        String sql = "INSERT INTO test.temperature (date, temperatur, time) VALUES (?,?,?)";
        preparedStatement = connect.prepareStatement(sql);

        preparedStatement.setDate(1, request.getRequestDate());
        preparedStatement.setString(2, request.getTemperature() + "@");
        preparedStatement.setString(3, request.getRequestTime() + "@");
    }

    private static String getDatabaseTable(Request request)
    {
        DatabaseTables table = DatabaseTables.fetchDatabaseTable(request.getId());
        return table.getTable();
    }

    public static class DatabaseBuilder
    {
        public static DbView initDatabase()
        {
            try
            {
                return new DatabaseConnection();
            }
            catch (ClassNotFoundException e)
            {
                throw new IllegalStateException("Failed to initate database: ", e);
            }

        }
    }
}
