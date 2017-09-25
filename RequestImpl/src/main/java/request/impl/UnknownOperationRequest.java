package request.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jsonserver.common.Utils.Utilities;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.RequestId;
import jsonserver.common.json.JsonResponseHandler;
import jsonserver.common.view.Request;

import java.sql.Date;

import static jsonserver.common.Utils.Utilities.REQUEST_ID;
import static jsonserver.common.Utils.Utilities.REQUEST_TYPE;

/**
 * Created by Foten on 4/23/2017.
 */
public class UnknownOperationRequest implements Request
{
    private final RequestId myRequestId;
    private final String myDate;
    private final ExpenseUser myUser;
    private final String myAction;

    public UnknownOperationRequest(String jsonObjectAsString)
    {
        JsonParser parser = new JsonParser();
        JsonElement JsonElement = parser.parse(jsonObjectAsString);
        JsonObject jsonObject = JsonElement.getAsJsonObject();

        JsonObject id = jsonObject.get("id").getAsJsonObject();
        myRequestId = new RequestId(id.get(REQUEST_ID).getAsString());
        myAction = jsonObject.get(REQUEST_TYPE).getAsString();
        myDate = jsonObject.get("requestDate").getAsString();

        if(jsonObject.get("user") != null)
        {
            JsonObject user = jsonObject.get("user").getAsJsonObject();
            myUser = new ExpenseUser(
                    user.get("userId").getAsString(),
                    user.get("username").getAsString(),
                    user.get("password").getAsString());
        }
        else
        {
            myUser = new ExpenseUser("unknown", "unknown", "unknown");
        }

    }

    @Override
    public RequestId getId()
    {
        return myRequestId;
    }

    @Override
    public Date getRequestDate()
    {
        return Date.valueOf(myDate);
    }

    @Override
    public boolean isValid()
    {
        return true;
    }

    @Override
    public ExpenseUser getUser()
    {
       return myUser;
    }

    public JsonObject generateResponse()
    {
        JsonObject response = JsonResponseHandler.createDefaultResponse(0, this, myAction);
        response.addProperty("reason", "Unknown operation: " + myAction + " for requestId" + myRequestId);
        addUserToJson(response);

        return response;
    }

    private void addUserToJson(JsonObject response)
    {
        JsonObject user = new JsonObject();
        user.addProperty("userId", myUser.getUserId());
        user.addProperty("username", myUser.getUsername());
        user.addProperty("password", myUser.getPassword());

        response.add("user", user);
    }
}
