package Connection.internal;

import jsonserver.common.containers.ExpensesContainer;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.Expenses;
import jsonserver.common.datatype.Threshold;
import jsonserver.common.view.DeleteRequest;
import jsonserver.common.view.Request;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by lofie on 2017-09-30.
 */
public class ExpensesConnection
{
    private static final Logger LOGGER = Logger.getLogger(ExpensesConnection.class);
    private final Connection myConnect;
    public ExpensesConnection(Connection connect)
    {
        myConnect = connect;
    }

    public int updateExpensesAndThreshold(ExpensesContainer expensesContainer, Request request) throws SQLException
    {
        Expenses expensesToUpdate = expensesContainer.getFirstSpeculatedExpense();
        Threshold thresholdToUpdate = expensesContainer.getFirstSpeculatedThreshold();

        int sqlResult = 0;
        if (expensesToUpdate != null)
        {
            sqlResult = insertExpensesValues(expensesToUpdate, request);
            boolean success = sqlResult != 0;
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

    public int removeData(DeleteRequest deleteRequest, List<String> idsToBeRemoved) throws SQLException
    {
        ExpenseUser user = deleteRequest.getUser();
        if (idsToBeRemoved.isEmpty())
        {
            LOGGER.info("Could not find any matching expense with id: " + idsToBeRemoved + "\nBreaking");
            return 0;
        }

        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM %s WHERE uuid IN (");

        IntStream.range(0, idsToBeRemoved.size())
                .mapToObj(i -> (i >= idsToBeRemoved.size() - 1 ? "?) AND username=?;" : "?,"))
                .forEach(sqlBuilder::append);
        String sql = sqlBuilder.toString();

        PreparedStatement preparedStatement = myConnect.prepareStatement(String.format(sql, "test.expenses"));

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

    public List<Expenses> readExpensesValue(Request request) throws SQLException
    {
        String query = "SELECT * FROM test.expenses WHERE username=?";

        PreparedStatement preparedStatement = myConnect.prepareStatement(query);
        preparedStatement.setString(1, request.getUser().getUsername());

        LOGGER.info("Statement: " + preparedStatement.toString());
        ResultSet resultSet = preparedStatement.executeQuery();

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

    public List<Threshold> readThresholdValue(Request request) throws SQLException
    {

        String query = "SELECT * FROM test.threshold WHERE username=?";

        PreparedStatement preparedStatement = myConnect.prepareStatement(query);
        preparedStatement.setString(1, request.getUser().getUsername());

        LOGGER.info("Statement: " + preparedStatement.toString());
        ResultSet resultSet = preparedStatement.executeQuery();

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

    private int insertExpensesValues(Expenses expenses, Request request) throws SQLException
    {
        ExpenseUser user = request.getUser();
        String sql = "INSERT INTO test.expenses (cost, costType, buyDate, comment, uuid, username) VALUES (?,?,?,?,?,?)";
        PreparedStatement preparedStatement = myConnect.prepareStatement(sql);

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

    public int updateThresholdValues(Threshold threshold, Request request) throws SQLException
    {
        String query = "UPDATE test.threshold SET currentCost=? WHERE month = ? AND type = ? AND username = ?";

        PreparedStatement preparedStatement = myConnect.prepareStatement(query);

        preparedStatement.setInt(1, threshold.getCurrentValue());
        preparedStatement.setInt(2, threshold.getMonth());
        preparedStatement.setString(3, threshold.getType());
        preparedStatement.setString(4, request.getUser().getUsername());

        LOGGER.info("Statement: " + preparedStatement.toString());
        int affectedRows = preparedStatement.executeUpdate();
        LOGGER.info("affected rows by statment: " + affectedRows);
        return affectedRows;
    }



}
