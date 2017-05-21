package operations;

import com.google.gson.JsonObject;
import jsonserver.common.json.JsonResponseHandler;
import jsonserver.common.view.Request;

import java.sql.SQLException;

/**
 * Created by Foten on 4/22/2017.
 */
public class UserWriteOperation extends DatabaseOperation<Request>
{
    public UserWriteOperation()
    {
        super("Write User");
    }

    @Override
    protected JsonObject performOperation(Request request) throws SQLException
    {
        int sqlResult = myDatabaseConnection.createUser(request);
        return JsonResponseHandler.createDefaultResponse(sqlResult, request, "Put");
    }
}
