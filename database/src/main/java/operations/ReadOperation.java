package operations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jsonserver.common.datatype.RequestId;
import jsonserver.common.json.JsonResponseHandler;
import jsonserver.common.view.GetRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

import static jsonserver.common.datatype.RequestId.ValidRequestIdEnum.EXPENSES;

/**
 *
 */
public class ReadOperation extends DatabaseOperation<GetRequest>
{
    public ReadOperation()
    {
        super("READ");
    }

    @Override
    protected JsonObject performOperation(GetRequest request) throws SQLException
    {
        ResultSet resultSet = myDatabaseConnection.executeGetRequest(request);
        JsonObject getResponse;

        if (resultSet != null)
        {
            getResponse = JsonResponseHandler.createGetResponse(resultSet, request);

            if(EXPENSES.getId().equals(request.getId()))
            {
                JsonArray jsonArray = myDatabaseConnection.readthresholdValue(request);
                getResponse.add("ThresholdResult", jsonArray);
            }
        }
        else
        {
            getResponse = JsonResponseHandler.FAILED_EXECUTING_REQUEST;
        }
        return getResponse;
    }
}
