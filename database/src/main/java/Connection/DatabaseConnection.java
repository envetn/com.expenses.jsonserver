package Connection;

import com.google.gson.JsonObject;
import jsonserver.common.containers.ExpensesContainer;
import jsonserver.common.containers.TemperatureContainer;
import jsonserver.common.containers.UserContainer;
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
import static jsonserver.common.datatype.RequestId.ValidRequestIdEnum.TEMPERATURE;
import static jsonserver.common.datatype.RequestId.ValidRequestIdEnum.USER;

/**
 * Sql database for stuff
 */
public class DatabaseConnection implements DbView
{
    //TODO: This Connection.DatabaseConnection could be an OSGI services
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class);
    private final static Integer SQL_FAILED = 0;

    private Connection connect;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private static Date myDate;

    // http://www.vogella.com/tutorials/MySQLJava/article.html
    public DatabaseConnection() throws ClassNotFoundException
    {
        Class.forName("com.mysql.jdbc.Driver");
        //TODO keep track of existing tables in the DB
    }

    @Override
    public UserContainer createUserContainer(Request request) throws SQLException
    {
        openConnection();
        UserContainer userContainer = null;
        //TODO: What happens if the user is invalid?
        if (validateUser(request.getUser()))
        {
            List<Threshold> thresholds = readThresholdValue(request);
            List<Expenses> expenses = readExpensesValue(request);
            List<Temperature> temperature = readTemperatureValue(request);

            userContainer = UserContainer.newBuilder()
                    .setRequest(request)
                    .setExpenseContainer(new ExpensesContainer(expenses, thresholds))
                    .setTemperatureContainer(new TemperatureContainer(temperature))
                    .build();
        }
        else if (request.getUser() != null && USER.getId().equals(request.getId()))
        {
            userContainer = UserContainer.newBuilder()
                    .setRequest(request)
                    .build();
        }
        closeConnection();

        return userContainer;

    }

    @Override
    public JsonObject readFromContainer(UserContainer container)
    {
        //TODO: should I really split these? They could be one and the same
        Request request = container.getRequest();
        RequestId id = request.getId();

        JsonObject response = null;
        if (id.equals(EXPENSES.getId()))
        {
            ExpensesContainer expensesContainer = container.getExpensesContainer();
            response =  expensesContainer.createExpensesJsonObject((GetRequest) request);
        }
        else if(id.equals(TEMPERATURE.getId()))
        {
            TemperatureContainer temperatureContainer = container.getTemperatureContainer();
            response = temperatureContainer.readTemperature((GetRequest) request);
        }
        else if(id.equals(USER.getId()))
        {
            response = container.getUser();
        }

        return response;
    }

    @Override
    public JsonObject putIntoContainer(UserContainer container)
    {
        Request request = container.getRequest();
        RequestId id = request.getId();

        if (EXPENSES.getId().equals(id))
        {
            ExpensesContainer expensesContainer = container.getExpensesContainer();
            return expensesContainer.insertInto((ExpensePutRequest) request);
        }
        else if (USER.getId().equals(id))
        {
            return container.updateUser();
        }
        else
        {
            TemperatureContainer temperatureContainer = container.getTemperatureContainer();
            return temperatureContainer.putInto((TemperaturePutRequest) request);
        }
    }

    @Override
    public boolean saveDatabaseChanges(String type, UserContainer container) throws SQLException
    {
        Request request = container.getRequest();
        if ("Get".equals(type))
        {
            return true;
        }

        int sqlResult;
        openConnection();

        if (TEMPERATURE.getId().equals(request.getId()))
        {
            sqlResult = updateTemperature(container);
        }
        else if (USER.getId().equals(request.getId()))
        {
            LOGGER.info("Trying to update/create user...");

            if (request.getRequestType().equals("Put"))
            {
                sqlResult = createUser(container, request);
            }
            else
            {
                sqlResult = removeUserData(container, request);
            }

        }
        else
        {
            ExpensesContainer expensesContainer = container.getExpensesContainer();
            if (request.getRequestType().equals("Put"))
            {
                sqlResult = updateExpensesAndThreshold(expensesContainer, request);
            }
            else
            {

                List<String> idToBeRemoved = extractIdsToBeRemoved(container);
                sqlResult = removeData((DeleteRequest) request, idToBeRemoved);

                if (sqlResult != SQL_FAILED)
                {
                    List<Threshold> listOfSpeculatedThresholds = expensesContainer.getListOfSpeculatedThresholds();
                    for (Threshold speculatedThreshold : listOfSpeculatedThresholds)
                    {
                        sqlResult = updateThresholdValues(speculatedThreshold, request);

                        if (sqlResult == SQL_FAILED)
                        {
                            LOGGER.warn("Failed to update Threshold: " + speculatedThreshold);
                        }
                    }
                }
            }
            expensesContainer.clearSpecualted();
        }

        return sqlResult != SQL_FAILED;
    }

    private int removeUserData(UserContainer container, Request request)
    {
        // remove every expenses connected to the user.

        // Remove every temperature connected to the user

        // Last, remove the user itself
        LOGGER.error("Removing user data not supported in this release");

        return 0;
    }

    private List<String> extractIdsToBeRemoved(UserContainer container)
    {
        List<Expenses> listOfSpeculatedExpenses = container.getExpensesContainer().getListOfspeculatedExpenses();

        List<String> idsToRemove = new ArrayList<>(listOfSpeculatedExpenses.size());
        for (Expenses speculatedExpense : listOfSpeculatedExpenses)
        {
            idsToRemove.add(speculatedExpense.getUuid());
        }
        return idsToRemove;
    }

    @Override
    public JsonObject removeFromContainer(UserContainer container)
    {
        Request request = container.getRequest();
        RequestId id = request.getId();

        if (EXPENSES.getId().equals(id))
        {
            ExpensesContainer expensesContainer = container.getExpensesContainer();
            return expensesContainer.removeFrom((DeleteRequest) request);
        }
        else if (USER.getId().equals(id))
        {
            return container.removeUser();
        }
//        else
//        {
//            TemperatureContainer temperatureContainer = container.getTemperatureContainer();
//            return temperatureContainer.putInto((TemperaturePutRequest) request);
//        }
        return null;
    }

    private boolean validateUser(ExpenseUser user) throws SQLException
    {
        String sqlString = "SELECT * FROM test.expenseuser where username = ? AND passwd = ?";
        preparedStatement = connect.prepareStatement(sqlString);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());

        LOGGER.info(sqlString);
        resultSet = preparedStatement.executeQuery();

        return resultSet.next();
    }

    private void openConnection()
    {
        try
        {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/test?user=root&password=root&amp;useUnicode=true&characterEncoding=utf8");
            statement = connect.createStatement();
        }
        catch (SQLException e)
        {
            LOGGER.info("Connection.DatabaseConnection . SQLException " + e.toString());
        }
    }

    private void closeConnection()
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
            LOGGER.info("Failed to close database connection: " + e.toString());
        }
    }

    private int updateExpensesAndThreshold(ExpensesContainer expensesContainer, Request request) throws SQLException
    {
        Expenses expensesToUpdate = expensesContainer.getFirstSpeculatedExpense();
        Threshold thresholdToUpdate = expensesContainer.getFirstSpeculatedThreshold();

        int sqlResult = SQL_FAILED;
        if (expensesToUpdate != null)
        {
            sqlResult = insertExpensesValues(expensesToUpdate, request);
            boolean success = sqlResult != SQL_FAILED;
            LOGGER.info("Trying to update expenses. Result: " + success);

            if (thresholdToUpdate != null && success)
            {
                sqlResult = updateThresholdValues(thresholdToUpdate, request);
            }
        }
        else
        {
            LOGGER.warn("No expenses to update!");
        }
        return sqlResult;
    }

    private int updateTemperature(UserContainer container) throws SQLException
    {
        int sqlResult = SQL_FAILED;
        TemperatureContainer temperatureContainer = container.getTemperatureContainer();
        Temperature temperatureToUpdate = temperatureContainer.getSpeculatedTemperature();
        if (temperatureToUpdate != null)
        {
            sqlResult = insertTemperatureValues(temperatureToUpdate);
            boolean success = sqlResult != SQL_FAILED;
            LOGGER.info("Trying to update Temperature. Result:  " + success);
        }
        else
        {
            LOGGER.info("No temperature to update");
        }
        return sqlResult;
    }

    private int createUser(UserContainer container, Request createUserRequest) throws SQLException
    {
        if (container.isUserUpdated())
        {
            String database = getDatabaseTable(createUserRequest);
            ExpenseUser user = createUserRequest.getUser();

            String sql = "INSERT INTO %s (username, passwd, created) VALUES (?,?,?)";
            preparedStatement = connect.prepareStatement(String.format(sql, database));
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setDate(3, createUserRequest.getRequestDate());

            LOGGER.info("Statement: " + preparedStatement.toString());
            return preparedStatement.executeUpdate();
        }
        LOGGER.error("User has not been changed, no need to update");
        return SQL_FAILED;
    }

    public int removeData(DeleteRequest deleteRequest, List<String> idsToBeRemoved) throws SQLException
    {
        //What the F*** happens here?...
        String database = getDatabaseTable(deleteRequest);

        ExpenseUser user = deleteRequest.getUser();
        if (idsToBeRemoved.isEmpty())
        {
            LOGGER.info("Could not find any matching expense with id: " + idsToBeRemoved + "\nBreaking");
            return SQL_FAILED;
        }

        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM %s WHERE uuid IN (");

        IntStream.range(0, idsToBeRemoved.size())
                .mapToObj(i -> (i >= idsToBeRemoved.size() - 1 ? "?) AND username=?;" : "?,"))
                .forEach(sqlBuilder::append);
        String sql = sqlBuilder.toString();

        preparedStatement = connect.prepareStatement(String.format(sql, database));

        int id = 0;
        for (String idToRemove : idsToBeRemoved)
        {
            String replacedId = idToRemove.replace("'", "");
            preparedStatement.setString(++ id, replacedId);
        }
        preparedStatement.setString(++ id, user.getUsername());

        LOGGER.info("preparedStatement: " + preparedStatement.toString());
        return preparedStatement.executeUpdate();
    }


    private int insertExpensesValues(Expenses expenses, Request request) throws SQLException
    {

        ExpenseUser user = request.getUser();
        String sql = "INSERT INTO test.expenses (cost, costType, buyDate, comment, uuid, username) VALUES (?,?,?,?,?,?)";
        preparedStatement = connect.prepareStatement(sql);

        preparedStatement.setString(1, expenses.getCost());
        preparedStatement.setString(2, expenses.getCostType());
        preparedStatement.setDate(3, expenses.getBuyDate());
        preparedStatement.setString(4, expenses.getComment());
        preparedStatement.setString(5, expenses.getUuid());
        preparedStatement.setString(6, user.getUsername());

        LOGGER.info("Statement: " + preparedStatement.toString());
        return preparedStatement.executeUpdate();
        //TODO, if no table with threshold values, what to do?
    }

    private int updateThresholdValues(Threshold threshold, Request request) throws SQLException
    {
        String query = "UPDATE test.threshold SET currentCost=? WHERE month = ? AND type = ? AND username = ?";

        preparedStatement = connect.prepareStatement(query);

        preparedStatement.setInt(1, threshold.getCurrentValue());
        preparedStatement.setInt(2, threshold.getMonth());
        preparedStatement.setString(3, threshold.getType());
        preparedStatement.setString(4, request.getUser().getUsername());

        LOGGER.info("Statement: " + preparedStatement.toString());
        int affectedRows = preparedStatement.executeUpdate();
        LOGGER.info("affected rows by statment: " + affectedRows);
        return affectedRows;
    }


    private List<Temperature> readTemperatureValue(Request request) throws SQLException
    {
        //Todo: only read temperature connected to username
        String query = "SELECT * FROM test.temperature";

        preparedStatement = connect.prepareStatement(query);

        LOGGER.info("Statement: " + preparedStatement.toString());
        resultSet = preparedStatement.executeQuery();

        List<Temperature> listOfTemperature = new ArrayList<>();

        while (resultSet.next())
        {
            Date date = Date.valueOf(resultSet.getString("date"));
            String unsortedTemperature = resultSet.getString("temperatur");
            String unsortedTimeStamp = resultSet.getString("time");
            Temperature temperature = new Temperature(date, unsortedTemperature, unsortedTimeStamp, false);

            listOfTemperature.add(temperature);
        }
        return listOfTemperature;
    }


    private List<Expenses> readExpensesValue(Request request) throws SQLException
    {
        String query = "SELECT * FROM test.expenses WHERE username=?";

        preparedStatement = connect.prepareStatement(query);
        preparedStatement.setString(1, request.getUser().getUsername());

        LOGGER.info("Statement: " + preparedStatement.toString());
        resultSet = preparedStatement.executeQuery();

        List<Expenses> listOfExpenses = new ArrayList<>();

        while (resultSet.next())
        {
            String cost = resultSet.getString("cost");
            String costType = resultSet.getString("costType");
            Date buyDate = Date.valueOf(resultSet.getString("buyDate"));
            String comment = resultSet.getString("comment");
            String uuid = resultSet.getString("uuid");

            Expenses expenses = new Expenses(cost, costType, buyDate, comment, uuid, true);

            listOfExpenses.add(expenses);


        }
        return listOfExpenses;
    }

    private List<Threshold> readThresholdValue(Request request) throws SQLException
    {

        String query = "SELECT * FROM test.threshold WHERE username=?";

        preparedStatement = connect.prepareStatement(query);
        preparedStatement.setString(1, request.getUser().getUsername());

        LOGGER.info("Statement: " + preparedStatement.toString());
        resultSet = preparedStatement.executeQuery();

        List<Threshold> thresholds = new ArrayList<>();

        while (resultSet.next())
        {
            int currentValue = resultSet.getInt("currentCost");
            int threshold = resultSet.getInt("threshold");

            String type = resultSet.getString("type");
            int month = resultSet.getInt("month");
            String username = resultSet.getString("username");
            thresholds.add(new Threshold(currentValue, threshold, month, type, username));
        }
        return thresholds;
    }

    /**
     * Fetch threshold by Username and month.
     */
    private int insertTemperatureValues(Temperature temperature) throws SQLException
    {
        if (myDate == null)
        {
            fetchLastTemperatureDate();
        }

        Date incomingDate = temperature.getDate();

        if (myDate.toString().equals(incomingDate.toString()))
        {
            appendData(temperature);
        }
        else
        {
            myDate = incomingDate;
            insertData(temperature);
        }
        LOGGER.info("Statement: " + preparedStatement.toString());
        return preparedStatement.executeUpdate();
    }

    private void fetchLastTemperatureDate() throws SQLException
    {
        resultSet = statement.executeQuery("SELECT id, date FROM test.temperature ORDER BY id DESC LIMIT 1 ");
        if (resultSet.next())
        {
            myDate = resultSet.getDate("date");
        }
    }

    private void appendData(Temperature temperature) throws SQLException
    {
        String time = temperature.getTime();
        String temp = temperature.getTemperature();
        Date temperatureDate = temperature.getDate();

        String sql = "UPDATE test.temperature SET time=CONCAT(time,?), temperatur=CONCAT(temperatur,?)  WHERE date=?  LIMIT 1";
        preparedStatement = connect.prepareStatement(sql);

        preparedStatement.setString(1, time + "@");
        preparedStatement.setString(2, temp + "@");
        preparedStatement.setDate(3, temperatureDate);
    }

    private void insertData(Temperature temperature) throws SQLException
    {
        String time = temperature.getTime();
        String temp = temperature.getTemperature();
        Date temperatureDate = temperature.getDate();
        String sql = "INSERT INTO test.temperature (date, temperatur, time) VALUES (?,?,?)";
        preparedStatement = connect.prepareStatement(sql);

        preparedStatement.setDate(1, temperatureDate);
        preparedStatement.setString(2, temp + "@");
        preparedStatement.setString(3, time + "@");
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
