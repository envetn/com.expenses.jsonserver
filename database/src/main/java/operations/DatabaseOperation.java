package operations;

import Connection.DatabaseConnection;
import com.google.gson.JsonObject;
import jsonserver.common.json.JsonDecoder;
import jsonserver.common.view.Request;
import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * Created by Foten on 4/21/2017.
 */
public abstract class DatabaseOperation<T extends Request>
{
    private final String myOperation;
    private static final Logger logger = Logger.getLogger(DatabaseOperation.class);
    protected final DatabaseConnection myDatabaseConnection;

    protected DatabaseOperation(String operation)
    {
        myOperation = operation;
        try
        {
            myDatabaseConnection = new DatabaseConnection();
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("Failed to initiate database: ", e);
        }
    }

    protected abstract JsonObject performOperation(T request) throws SQLException;

    public JsonObject execute(T request)
    {
        myDatabaseConnection.openConnection();
        JsonObject response;
        try
        {
            response = performOperation(request);
        }
        catch (SQLException e)
        {
            logger.info("Failed with executeWrite: " + myOperation, e);
            response = JsonDecoder.FAILED_EXECUTING_REQUEST;
        }

        myDatabaseConnection.closeConnection();
        return response;
    }
}
