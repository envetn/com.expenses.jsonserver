package jsonserver.common.containers;

import com.google.gson.JsonObject;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.view.GetRequest;
import jsonserver.common.view.Request;

import static jsonserver.common.Utils.DateUtils.getCurrentTimestamp;

/**
 * Created by lofie on 2017-05-21.
 */
public class UserContainer
{
    private final Request myRequest;

    private final ExpensesContainer myExpenesContainer;
    private final TemperatureContainer myTemperatureContainer;
    private boolean updateUser = false;

    private UserContainer(Builder builder)
    {
        myRequest = builder.myRequest;

        myTemperatureContainer = builder.myTemperatureContainer;
        myExpenesContainer = builder.myExpenesContainer;
    }

    public Request getRequest()
    {
        return myRequest;
    }


    public TemperatureContainer getTemperatureContainer()
    {
        return myTemperatureContainer;
    }

    public ExpensesContainer getExpensesContainer()
    {
        return myExpenesContainer;
    }

    public boolean isUserUpdated()
    {
        return updateUser;
    }

    public JsonObject getUser()
    {
        ExpenseUser user = myRequest.getUser();

        JsonObject userHeader = createUserHeader("Get-User", "GET");
        JsonObject userJsonObject= createUserJsonObject(user);
        JsonObject expensesJsonObject = myExpenesContainer.createExpensesJsonObject((GetRequest) myRequest);
        JsonObject temperatureJsonObject = myTemperatureContainer.readTemperature((GetRequest) myRequest);

        JsonObject jsonGetData = new JsonObject();

        jsonGetData.add("user", userJsonObject);
        jsonGetData.add("expenses", expensesJsonObject);
        jsonGetData.add("temperature", temperatureJsonObject);

        userHeader.add("Get-Data", jsonGetData);

        return userHeader;
    }

    public JsonObject updateUser()
    {
        updateUser = true;
        ExpenseUser user = myRequest.getUser();

        JsonObject userHeader = createUserHeader("Put-User", "PUT");
        JsonObject userJsonObject = createUserJsonObject(user);

        userHeader.add("Get-Data", userJsonObject);
        return userHeader;
    }

    public JsonObject removeUser()
    {
        updateUser = true;
        ExpenseUser user = myRequest.getUser();

        JsonObject userHeader = createUserHeader("Delete-User", "DELETE");
        JsonObject userJsonObject = createUserJsonObject(user);

        userHeader.add("Remove-Data", userJsonObject);

        return userHeader;
    }

    private JsonObject createUserHeader(String reason, String type)
    {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("Response", "Success");
        jsonResponse.addProperty("Time", getCurrentTimestamp());
        jsonResponse.addProperty("where", "User Response");
        jsonResponse.addProperty("Reason", type);
        jsonResponse.addProperty("Type", reason);
        return jsonResponse;
    }


    private JsonObject createUserJsonObject(ExpenseUser user)
    {
        JsonObject object = new JsonObject();
        object.addProperty("username", user.getUsername());
        object.addProperty("userId", user.getUserId());
        object.addProperty("passwd", "*****");
        return object;
    }


    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Builder()
        {
            // empty
        }

        private Request myRequest;

        private ExpensesContainer myExpenesContainer;
        private TemperatureContainer myTemperatureContainer;

        public Builder setRequest(Request request)
        {
            myRequest = request;
            return this;
        }

        public Builder setExpenseContainer(ExpensesContainer expenseContainer)
        {
            myExpenesContainer = expenseContainer;
            return this;
        }

        public Builder setTemperatureContainer(TemperatureContainer temperatureContainer)
        {
            myTemperatureContainer = temperatureContainer;
            return this;
        }

        public UserContainer build()
        {
            return new UserContainer(this);
        }
    }
}
