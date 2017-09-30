package request.requestcreator;

import request.impl.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import jsonserver.common.view.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Foten on 4/10/2017.
 */
public enum JsonRequestFactory
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
                public Request createRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, GetDataRequestImpl.class);
                }
            },

    EXPENSES_PUT("Expenses-Put")
            {
                @Override
                public Request createRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, ExpensesPutRequestImpl.class);
                }
            },

    EXPENSES_REMOVE("Expenses-Delete")
            {
                @Override
                public Request createRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, DeleteRequestImpl.class);
                }
            },


    TEMPERATURE_GET("Temperature-Get")
            {
                @Override
                public Request createRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, GetDataRequestImpl.class);
                }
            },

    TEMPERATURE_PUT("Temperature-Put")
            {
                @Override
                public Request createRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, TemperaturePutRequestImpl.class);
                }

            },

//    TEMPERATURE_REMOVE("Temperature-Delete")
//            {
//                @Override
//                public Request createRequest(String jsonObject) throws IOException
//                {
//                    throw new IllegalArgumentException("Not supported");
//                }
//            },

    USER_PUT("User-Put")
            {
                @Override
                public Request createRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, CreateUserRequest.class);
                }
            },
    USER_GET("User-Get")
            {
                @Override
                public Request createRequest(String jsonObject) throws IOException
                {
                    return OBJECT_MAPPER.readValue(jsonObject, GetDataRequestImpl.class);
                }
            },

    UNKNOWN_OPERATION("Unknown-Operation")
            {
                @Override
                public Request createRequest(String jsonObjectAsString)
                {
                    return new UnknownOperationRequest(jsonObjectAsString);
                }

            };

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Map<String, JsonRequestFactory> REQUEST_CREATOR_MAP;

    static
    {
        Map<String, JsonRequestFactory> myTemporaryMap = new HashMap<>();

        for (JsonRequestFactory jsonCreator : JsonRequestFactory.values())
        {
            myTemporaryMap.put(jsonCreator.myName, jsonCreator);
        }

        REQUEST_CREATOR_MAP = ImmutableMap.copyOf(myTemporaryMap);
    }

    private final String myName;

    JsonRequestFactory(String myName)
    {
        this.myName = myName;
    }

    /**
     * @param jsonObjectAsString
     * @return
     * @throws IOException
     */
    public abstract Request createRequest(String jsonObjectAsString) throws IOException;


    /**
     * Get {@link JsonRequestFactory} for give name
     * <p>
     * The name of the {@link JsonRequestFactory} is a combination of the RequestId and RequestType.
     * <p>
     * Example: Expenses-Get will give the JsonRequestFactory for Expenses Get request
     *
     * @param name
     * @return
     */
    public static JsonRequestFactory getRequestCreatorByName(String name)
    {
        JsonRequestFactory JsonRequestFactory = REQUEST_CREATOR_MAP.get(name);
        if (JsonRequestFactory == null)
        {
            JsonRequestFactory = UNKNOWN_OPERATION;
        }
        return JsonRequestFactory;
    }

}
