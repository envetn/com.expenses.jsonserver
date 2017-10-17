package Connection;

import Connection.internal.ExpensesConnection;
import Connection.internal.TemperatureConnection;
import Connection.internal.UserConnection;
import cache.MemoryCache;
import com.google.gson.JsonObject;
import jsonserver.common.containers.ExpensesContainer;
import jsonserver.common.containers.TemperatureContainer;
import jsonserver.common.containers.UserContainer;
import jsonserver.common.datatype.*;
import org.apache.log4j.Logger;

import java.sql.*;
import java.sql.Date;
import java.util.*;

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
    private static Date myDate;

    private static final MemoryCache<ExpenseUser, UserContainer> USER_CONTAINER_MEMORY_CACHE = new MemoryCache<>();
    private boolean myUseCahce;

    // http://www.vogella.com/tutorials/MySQLJava/article.html
    public DatabaseConnection(boolean useCahce) throws ClassNotFoundException
    {
        Class.forName("com.mysql.jdbc.Driver");
        myUseCahce = useCahce;
        //TODO keep track of existing tables in the DB
    }

    @Override
    public UserContainer createUserContainer(Request request) throws SQLException
    {
        UserContainer userContainer = getUserContainerFromCache(request);
        if (userContainer == null)
        {
            LOGGER.info("User container was not found in cache, creating it now..");
            openConnection();
            //TODO: What happens if the user is invalid?
            UserConnection userConnection = new UserConnection(connect);
            if (userConnection.doesUserExist(request))
            {
                if(userConnection.isUserAllowedToExist(request))
                {
                    ExpensesConnection expensesConnection = new ExpensesConnection(connect);
                    TemperatureConnection temperatureConnection = new TemperatureConnection(connect, myDate, statement);

                    List<Threshold> thresholds = expensesConnection.readThresholdValue(request);
                    List<Expenses> expenses = expensesConnection.readExpensesValue(request);
                    List<Temperature> temperature = temperatureConnection.readTemperatureValue(request);

                    userContainer = UserContainer.newBuilder()
                            .setRequest(request)
                            .setExpenseContainer(new ExpensesContainer(expenses, thresholds))
                            .setTemperatureContainer(new TemperatureContainer(temperature))
                            .build();
                }
                else
                {
                    LOGGER.info("User was not allowed to exists. Doing nothing...");
                    userContainer = UserContainer.newBuilder()
                            .setRequest(request)
                            .setErrorMessage("User already exists and is not allowed to exist for this request")
                            .build();
                }

            }
            else if (request.getUser() != null && USER.getId().equals(request.getId()))
            {
                userContainer = UserContainer.newBuilder()
                        .setRequest(request)
                        .build();
            }
            closeConnection();
            saveUserContainerToCache(request, userContainer);
        }
        else
        {
            LOGGER.info("!!!! Cache was used !!!\n Rebuilding container");
            userContainer = UserContainer.newBuilder()
                    .setRequest(request)
                    .setUpdateUser(userContainer.isUserUpdated())
                    .setExpenseContainer(userContainer.getExpensesContainer())
                    .setTemperatureContainer(userContainer.getTemperatureContainer())
                    .build();
        }

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
            response = expensesContainer.createExpensesJsonObject((GetRequest) request);
        }
        else if (id.equals(TEMPERATURE.getId()))
        {
            TemperatureContainer temperatureContainer = container.getTemperatureContainer();
            response = temperatureContainer.readTemperature((GetRequest) request);
        }
        else if (id.equals(USER.getId()))
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
            LOGGER.info("Get request, nothing to save..");
            return true;
        }

        int sqlResult;
        openConnection();

        if (TEMPERATURE.getId().equals(request.getId()))
        {
            TemperatureConnection temperatureConnection = new TemperatureConnection(connect, myDate, statement);
            sqlResult = temperatureConnection.updateTemperature(container);
        }
        else if (USER.getId().equals(request.getId()))
        {
            LOGGER.info("Trying to update/create user...");
            UserConnection userConnection = new UserConnection(connect);

            if (request.getRequestType().equals("Put"))
            {
                sqlResult = userConnection.createUser(container, request);
            }
            else
            {
                sqlResult = userConnection.removeUserData(container, request);
            }
        }
        else
        {
            //TODO ExpensesConnection can handle all the Expenses data towards teh database
            ExpensesConnection expensesConnection = new ExpensesConnection(connect);
            ExpensesContainer expensesContainer = container.getExpensesContainer();
            if (request.getRequestType().equals("Put"))
            {
                sqlResult = expensesConnection.updateExpensesAndThreshold(expensesContainer, request);
            }
            else
            {
                //remove
                List<String> idToBeRemoved = extractIdsToBeRemoved(container);
                sqlResult = expensesConnection.removeData((DeleteRequest) request, idToBeRemoved);

                if (sqlResult != SQL_FAILED)
                {
                    List<Threshold> listOfSpeculatedThresholds = expensesContainer.getListOfSpeculatedThresholds();
                    for (Threshold speculatedThreshold : listOfSpeculatedThresholds)
                    {
                        sqlResult = expensesConnection.updateThresholdValues(speculatedThreshold, request);

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
//            how shall I remove stuff from temperature?
//            TemperatureContainer temperatureContainer = container.getTemperatureContainer();
//            return temperatureContainer.putInto((TemperaturePutRequest) request);
//        }
        return null;
    }

    @Override
    public void cleanCache()
    {
        // Do I really need the cleanup?
        USER_CONTAINER_MEMORY_CACHE.cleanup();
    }

    private UserContainer getUserContainerFromCache(Request request)
    {
        if (myUseCahce)
        {
            ExpenseUser user = request.getUser();
            return USER_CONTAINER_MEMORY_CACHE.get(user);
        }
        return null;
    }

    private void saveUserContainerToCache(Request request, UserContainer userContainer)
    {
        if (myUseCahce)
        {
            ExpenseUser user = request.getUser();
            USER_CONTAINER_MEMORY_CACHE.put(user, userContainer);
        }
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
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            LOGGER.info("Failed to close database connection: " + e.toString());
        }
    }

    public static class DatabaseBuilder
    {
        public static DbView initDatabase(boolean useCache)
        {
            try
            {
                return new DatabaseConnection(useCache);
            }
            catch (ClassNotFoundException e)
            {
                throw new IllegalStateException("Failed to initate database: ", e);
            }

        }
    }
}
