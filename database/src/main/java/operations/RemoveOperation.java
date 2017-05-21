package operations;

import com.google.gson.JsonObject;
import jsonserver.common.json.JsonResponseHandler;
import jsonserver.common.view.DeleteRequest;

import java.sql.SQLException;

/**
 * Created by Foten on 4/22/2017.
 */
public class RemoveOperation extends DatabaseOperation<DeleteRequest>
{
    public RemoveOperation()
    {
        super("Remove");
    }

    @Override
    protected JsonObject performOperation(DeleteRequest request) throws SQLException
    {
        int sqlResult = myDatabaseConnection.removeData(request);
        return createResponse(sqlResult, request);
    }

    private JsonObject createResponse(int sqlResult, DeleteRequest request)
    {
        return JsonResponseHandler.createDefaultResponse(sqlResult, request, "Delete");
    }
}
