package operations;

import com.google.gson.JsonObject;
import jsonserver.common.json.JsonResponseHandler;
import jsonserver.common.view.Temperature.TemperaturePutRequest;

import java.sql.SQLException;

/**
 * Created by Foten on 4/22/2017.
 */
public class TemperatureWriteOperation extends DatabaseOperation<TemperaturePutRequest>
{
    public TemperatureWriteOperation()
    {
        super("Temperature Write");
    }

    @Override
    protected JsonObject performOperation(TemperaturePutRequest request) throws SQLException
    {
        int sqlResult = myDatabaseConnection.insertTemperatureValues(request);
        return JsonResponseHandler.createDefaultResponse(sqlResult, request, "Put");
    }
}
