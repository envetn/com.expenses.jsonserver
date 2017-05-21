package jsonserver.common.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import java.sql.Date;

import static jsonserver.common.Utils.Utilities.DATE_KEY;
import static jsonserver.common.Utils.Utilities.getTimestamp;

/**
 * Created by Foten on 11/30/2016.
 */
public abstract class JsonDecoder
{
    public static final JsonObject FAILED_CREATING_MESSAGE = createJsonHeader("Failed", "com.expenses.sparkexample.services.Socket server", "Json sent towards server was invalid");
    public static final JsonObject FAILED_EXECUTING_REQUEST = createJsonHeader("Failed", "com.expenses.sparkexample.services.Socket server", "Failed to execute request towards database");
    static ObjectMapper mapper = new ObjectMapper();

    static JsonObject createJsonHeader(String responseInfo, String where, String reason)
    {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("Response", responseInfo);
        jsonResponse.addProperty("Time", getTimestamp());
        jsonResponse.addProperty("where", where );
        jsonResponse.addProperty("Reason", reason);

        return jsonResponse;
    }

    static Date stringToDate(JsonObject jsonObject)
    {
        String date = jsonObject.get(DATE_KEY).getAsString();
        return Date.valueOf(date);
    }
}
