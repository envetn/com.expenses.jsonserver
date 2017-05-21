package jsonserver.common.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jsonserver.common.datatype.RequestId;
import org.apache.log4j.Logger;
import jsonserver.common.view.Request;

import java.sql.ResultSet;
import java.sql.SQLException;

import static jsonserver.common.Utils.Utilities.SQL_SUCCESS;
import static jsonserver.common.datatype.RequestId.ValidRequestIdEnum.EXPENSES;
import static jsonserver.common.datatype.RequestId.ValidRequestIdEnum.TEMPERATURE;

/**
 * Created by olof on 2016-07-09.
 */
public class JsonResponseHandler extends JsonDecoder
{
    private static final Logger logger = Logger.getLogger(JsonResponseHandler.class);

    public static JsonObject createGetResponse(ResultSet resultSet, Request request)
    {
        int fetchedRows = resultSet != null ? SQL_SUCCESS : 0;
        if (fetchedRows != SQL_SUCCESS)
        {
            return createDefaultResponse(fetchedRows, request, "Get");
        }

        return determineAndCreateResponse(resultSet, request);
    }

    public static JsonObject createGetResponseExpenses(ResultSet resultSet)
    {
        try
        {
            return createExpenseResponse(resultSet);
        }
        catch (SQLException e)
        {
            logger.info("Failed to create response for: ");
            return JsonDecoder.FAILED_EXECUTING_REQUEST;
        }
    }

    public static JsonObject createGetResponseTemperature(ResultSet resultSet)
    {
        try
        {
            return createTemperatureResponse(resultSet);
        }
        catch (SQLException e)
        {
            logger.info("Failed to create response for: ");
            return JsonDecoder.FAILED_EXECUTING_REQUEST;
        }
    }

    private static JsonObject determineAndCreateResponse(ResultSet resultSet, Request request)
    {
        try
        {

            if(EXPENSES.getId().equals(request.getId()))
            {
                return createExpenseResponse(resultSet);
            }
            else if(TEMPERATURE.getId().equals(request.getId()))
            {
                return createTemperatureResponse(resultSet);
            }
        }
        catch (SQLException e)
        {
            logger.info("Exception during response creation: ", e);
        }
        logger.info("Failed to create response for: " + request.getId());
        return JsonDecoder.FAILED_EXECUTING_REQUEST;
    }

    private static JsonObject createExpenseResponse(ResultSet resultSet) throws SQLException
    {
        JsonObject response = createJsonHeader("Success", "Expenses response", "Get");
        response.addProperty("Type", "Get-Expenses");
        JsonArray jsonArray = new JsonArray();

        while (resultSet.next())
        {
            JsonObject object = new JsonObject();
            object.addProperty("cost", resultSet.getString("cost"));
            object.addProperty("costType", resultSet.getString("costType"));
            object.addProperty("buyDate", resultSet.getString("buyDate"));
            object.addProperty("comment", resultSet.getString("comment"));
            object.addProperty("uuid", resultSet.getString("uuid"));
            //UUID

            jsonArray.add(object);
        }
        response.add("Get-Data", jsonArray);
        return response;
    }

    private static JsonObject createTemperatureResponse(ResultSet resultSet) throws SQLException
    {
        JsonObject response = createJsonHeader("Success", "Temperature response", "GET");
        response.addProperty("Type", "Get-Temperature");
        JsonArray jsonArray = new JsonArray();

        while (resultSet.next())
        {
            JsonObject object = new JsonObject();
            object.addProperty("date", resultSet.getString("date"));
            object.addProperty("temperature", resultSet.getString("temperatur"));
            object.addProperty("time", resultSet.getString("time"));
            //UUID - ??

            jsonArray.add(object);
        }
        response.add("Get-Data", jsonArray);
        return response;
    }

    public static JsonObject createDefaultResponse(int affectedRows, Request request, String action)
    {
        RequestId id = request.getId();

        String response = affectedRows >= SQL_SUCCESS ? "Success" : "Failed";
        JsonObject jsonResponse = createJsonHeader(response, id + " response", action);
        jsonResponse.addProperty("Type", action + "-" + id);
        jsonResponse.addProperty("AffectedRows", affectedRows);

        return jsonResponse;
    }
}
