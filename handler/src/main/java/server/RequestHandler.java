package server;

import Connection.DatabaseConnection;
import cache.MemoryCache;
import jsonserver.common.containers.UserContainer;
import jsonserver.common.exception.InvalidRequestException;
import request.requestcreator.JsonRequestFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jsonserver.common.Utils.Utilities;
import jsonserver.common.json.JsonDecoder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

import jsonserver.common.view.DbView;
import jsonserver.common.view.Request;
import server.internal.CachedRequest;

import static jsonserver.common.Utils.Utilities.REQUEST_ID;
import static jsonserver.common.Utils.Utilities.REQUEST_TYPE;

/**
 * Created by olof on 2016-07-09.
 */
public class RequestHandler
{

    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class);
    private static DbView myDatabaseView = null;

    public RequestHandler(boolean useCache)
    {
        if (myDatabaseView == null)
        {
            LOGGER.info("Initiating DatabaseConnection");
            myDatabaseView = DatabaseConnection.DatabaseBuilder.initDatabase(useCache);
        }
    }

    /**
     * For testing only
     */
    protected RequestHandler(DbView dbView)
    {
        myDatabaseView = dbView;
    }

    public UserContainer generateRequest(String strRequest) throws Exception
    {
        JsonRequestFactory jsonRequestFactory = validateRequestAndGetCreator(strRequest);
        Request request = jsonRequestFactory.createRequest(strRequest);
        if (request.isValid())
        {
            LOGGER.info("Generated request: " + request);
            return createUserContainer(request);
        }
        else
        {
            LOGGER.error("Request not valid!\nRequest: " + request);
        }
        return null;
    }

    public synchronized JsonObject executeRequest(UserContainer container)
    {
        String type = container.getRequest().getRequestType();
        JsonObject jsonResponse;
        if ("Get".equals(type))
        {
            LOGGER.debug("Executing Get request");
            jsonResponse = myDatabaseView.readFromContainer(container);
        }
        else if ("Put".equals(type))
        {
            LOGGER.debug("Executing Put request");
            jsonResponse = myDatabaseView.putIntoContainer(container);
        }
        else if ("Delete".equals(type))
        {
            LOGGER.debug("Executing Delete request");
            jsonResponse = myDatabaseView.removeFromContainer(container);
        }
        else
        {
            throw new InvalidRequestException("Unknown operation type: " + type);
        }

        return handleResponse(container, type, jsonResponse);
    }

    private JsonObject handleResponse(UserContainer container, String type, JsonObject jsonResponse)
    {
        if (jsonResponse == null)
        {
            String error = "request: " + container.getRequest() + "\nIs either invalid or null";
            LOGGER.error(error);
            jsonResponse = JsonDecoder.createFailedResponseWithMessage(error, "ExecuteRequest");
        }

        try
        {
            boolean changesSaved = myDatabaseView.saveDatabaseChanges(type, container);

            if (changesSaved)
            {
                return jsonResponse;
            }
            else
            {
                LOGGER.error("Failed to save changes to database");
                return JsonDecoder.createFailedResponseWithMessage("Failed to save changes to database", "Save database changes");
            }

        }
        catch (SQLException e)
        {
            LOGGER.error("Exception when trying to save changes to database", e);
            return JsonDecoder.createFailedResponseWithMessage(e.getMessage(), "Exception when trying to save changes to database");
        }
    }

    private UserContainer createUserContainer(Request request)
    {
        try
        {
            return myDatabaseView.createUserContainer(request);
        }
        catch (SQLException e)
        {
            LOGGER.error("SqlException when creating userContainer: ", e);
            throw new IllegalStateException("Unable to create UserContainer", e);
        }
    }

    private JsonRequestFactory validateRequestAndGetCreator(String request)
    {
        JsonObject jsonObject = validateJsonAndPrint(request);

        JsonObject id = jsonObject.get("id")
                .getAsJsonObject();
        String requestId = id.get(REQUEST_ID)
                .getAsString();
        String requestType = jsonObject.get(REQUEST_TYPE)
                .getAsString();
        // hm...
        String requestName = requestId + "-" + requestType;

        return JsonRequestFactory.getRequestCreatorByName(requestName);
    }

    private JsonObject validateJsonAndPrint(String request)
    {
        JsonParser parser = new JsonParser();
        JsonElement JsonElement = parser.parse(request);
        JsonObject jsonObject = JsonElement.getAsJsonObject();

        String prettyJsonString = Utilities.getPrettyJsonString(jsonObject);
        LOGGER.info("Received request: " + prettyJsonString);
        return jsonObject;
    }

    public void cleanCache()
    {
        if(myDatabaseView != null)
        {
            myDatabaseView.cleanCache();
        }
    }

}
