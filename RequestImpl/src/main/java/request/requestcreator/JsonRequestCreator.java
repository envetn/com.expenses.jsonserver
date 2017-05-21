package request.requestcreator;

import request.impl.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import jsonserver.common.view.DeleteRequest;
import jsonserver.common.view.Expense.ExpensePutRequest;
import jsonserver.common.view.GetRequest;
import jsonserver.common.view.Request;
import jsonserver.common.view.Temperature.TemperaturePutRequest;
import operations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Foten on 4/10/2017.
 */
public enum JsonRequestCreator
{
    /**
     * This enum could hold:
     * <p>
     * Creation of the request
     * Execution of database request
     * Creation of response
     */
    EXPENSES_GET("Expenses-Get")
            {
                @Override
                public Request generateRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, GetDataRequestImpl.class);
                }

                @Override
                public JsonObject executeRequest(Request request)
                {
                    ReadOperation readOperation = new ReadOperation();
                    return readOperation.execute((GetRequest) request);
                }

            },

    EXPENSES_PUT("Expenses-Put")
            {
                @Override
                public Request generateRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, ExpensesPutRequestImpl.class);
                }

                @Override
                public JsonObject executeRequest(Request request)
                {
                    ExpensesWriteOperation expensesWriteOperation = new ExpensesWriteOperation();
                    return expensesWriteOperation.execute((ExpensePutRequest) request);
                }

            },

    EXPENSES_REMOVE("Expenses-Delete")
            {
                @Override
                public Request generateRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, DeleteRequestImpl.class);
                }

                @Override
                public JsonObject executeRequest(Request request)
                {
                    RemoveOperation removeOperation = new RemoveOperation();
                    return removeOperation.execute((DeleteRequest) request);
                }

            },


    TEMPERATURE_GET("Temperature-Get")
            {
                @Override
                public Request generateRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, GetDataRequestImpl.class);
                }

                @Override
                public JsonObject executeRequest(Request request)
                {
                    ReadOperation readOperation = new ReadOperation();
                    return readOperation.execute((GetRequest) request);
                }

            },

    TEMPERATURE_PUT("Temperature-Put")
            {
                @Override
                public Request generateRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, TemperaturePutRequestImpl.class);
                }

                @Override
                public JsonObject executeRequest(Request request)
                {
                    TemperatureWriteOperation temperatureWriteOperation = new TemperatureWriteOperation();
                    return temperatureWriteOperation.execute((TemperaturePutRequest) request);
                }
            },

    TEMPERATURE_REMOVE("Temperature-Delete")
            {
                @Override
                public Request generateRequest(String jsonObject) throws IOException
                {
                    throw new IllegalArgumentException("Not supported");
                }

                @Override
                public JsonObject executeRequest(Request request)
                {
                    throw new IllegalArgumentException("Not supported");
                }

            },

    USER_PUT("User-Put")
            {
                @Override
                public Request generateRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, CreateUserRequest.class);
                }

                @Override
                public JsonObject executeRequest(Request request)
                {
                    UserWriteOperation userWriteOperation = new UserWriteOperation();
                    return userWriteOperation.execute(request);
                }

            },

    UNKNOWN_OPERATION("Unknown-Operation")
            {
                @Override
                public Request generateRequest(String jsonObjectAsString) throws IOException
                {
                    return new UnknownOperationRequest(jsonObjectAsString);
                }

                @Override
                public JsonObject executeRequest(Request request)
                {
                    return ((UnknownOperationRequest) request).generateResponse();
                }
            };

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Map<String, JsonRequestCreator> REQUEST_CREATOR_MAP;

    static
    {
        Map<String, JsonRequestCreator> myTemporaryMap = new HashMap<>();

        for (JsonRequestCreator jsonCreator : JsonRequestCreator.values())
        {
            myTemporaryMap.put(jsonCreator.myName, jsonCreator);
        }

        REQUEST_CREATOR_MAP = ImmutableMap.copyOf(myTemporaryMap);
    }

    private final String myName;

    JsonRequestCreator(String myName)
    {
        this.myName = myName;
    }

    /**
     * @param jsonObjectAsString
     * @return
     * @throws IOException
     */
    public abstract Request generateRequest(String jsonObjectAsString) throws IOException;

    /**
     * @param request
     * @return
     */
    public abstract JsonObject executeRequest(Request request);

    /**
     * Get {@link JsonRequestCreator} for give name
     * <p>
     * The name of the {@link JsonRequestCreator} is a combination of the RequestId and RequestType.
     * <p>
     * Example: Expenses-Get will give the JsonRequestCreator for Expenses Get request
     *
     * @param name
     * @return
     */
    public static JsonRequestCreator getRequestCreatorByName(String name)
    {

        JsonRequestCreator jsonRequestCreator = REQUEST_CREATOR_MAP.get(name);
        if (jsonRequestCreator == null)
        {
            jsonRequestCreator = UNKNOWN_OPERATION;
        }
        return jsonRequestCreator;
    }

}
