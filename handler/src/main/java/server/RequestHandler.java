package server;

import Connection.DatabaseConnection;
import cache.MemoryCache;
import request.requestcreator.JsonRequestCreator;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jsonserver.common.Utils.Utilities;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.json.JsonDecoder;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Collections;

import jsonserver.common.view.DbView;
import jsonserver.common.view.Request;
import server.internal.CachedRequest;

import static jsonserver.common.Utils.Utilities.REQUEST_ID;
import static jsonserver.common.Utils.Utilities.REQUEST_TYPE;
import static jsonserver.common.datatype.RequestId.ValidRequestIdEnum.USER;

/**
 * Created by olof on 2016-07-09.
 */
public class RequestHandler
{
    /**
     * Cache for already parsed jsonRequests
     */
    private static final MemoryCache<Integer, CachedRequest> REQUEST_CACHE = new MemoryCache<>();

    private static final Logger logger = Logger.getLogger(RequestHandler.class);
    private static DbView myDatabaseView = null;

    public RequestHandler()
    {
        if (myDatabaseView == null)
        {
            logger.info("Initiating DatabaseConnection");
            myDatabaseView = DatabaseConnection.DatabaseBuilder.initDatabase();
        }
    }

    private JsonObject validateJsonAndPrint(String request)
    {
        JsonParser parser = new JsonParser();
        JsonElement JsonElement = parser.parse(request);
        JsonObject jsonObject = JsonElement.getAsJsonObject();

        String prettyJsonString = Utilities.getPrettyJsonString(jsonObject);
        logger.info("Received request: " + prettyJsonString);
        return jsonObject;
    }

    public CachedRequest generateRequest(String strRequest)
    {
        CachedRequest cachedRequest = getFromMemoryCache(strRequest);

        if (cachedRequest == null)
        {
            try
            {
                JsonRequestCreator jsonRequestCreator = validateRequestAndGetCreator(strRequest);
                Request request = jsonRequestCreator.generateRequest(strRequest);
                logger.info("Generated request: " + request);
                cachedRequest = new CachedRequest(request, jsonRequestCreator);
                storeInMemoryCache(strRequest, cachedRequest);
                return cachedRequest;

            } catch (Exception e)
            {
                e.printStackTrace();
                logger.error("Failed to create request: " + e.toString());
                return null;
            }
        } else
        {
            logger.info("Request cache was used to fetch: " + cachedRequest);
        }

        return cachedRequest;

    }

    private JsonRequestCreator validateRequestAndGetCreator(String request)
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

        return JsonRequestCreator.getRequestCreatorByName(requestName);
    }

    /**
     * @param generatedRequest
     * @return
     */
    public synchronized JsonObject executeRequest(CachedRequest generatedRequest)
    {
        JsonObject response;
        if (validateRequest(generatedRequest.getRequest()))
        {
            //TODO handle this request async?
            response = generatedRequest.execute();
        }
        else
        {
            logger.error("request: " + generatedRequest + "\nIs either invalid or null");
            response = JsonDecoder.FAILED_CREATING_MESSAGE;
        }

        return response;
    }


    private CachedRequest getFromMemoryCache(String strRequest)
    {
        int hashCode = strRequest.hashCode();
        return REQUEST_CACHE.get(hashCode);
    }

    private void storeInMemoryCache(String request, CachedRequest cachedRequest)
    {
        int hashCode = request.hashCode();
        REQUEST_CACHE.put(hashCode, cachedRequest);
        logger.info("Data was stored in cache..");
    }

    private boolean validateRequest(Request request)
    {
        logger.info("Validating request...");
        boolean isValid = false;
        if (request != null && request.isValid())
        {
            ExpenseUser expenseUser = request.getUser();
            if (request.getId()
                    .equals(USER.getId()))
            {
                logger.info("Validating user: Should not exist");
                isValid = !validateUser(expenseUser, false);
            }
            else
            {
                logger.info("Validating user: Should exist");
                isValid = validateUser(expenseUser, true);
            }
        }
        return isValid;
    }

    private boolean validateUser(ExpenseUser expenseUser, boolean useCache)
    {
        ImmutableList<ExpenseUser> existingUsers;
        if (useCache)
        {
            existingUsers = myDatabaseView.getCachedUsers();
        }
        else
        {
            existingUsers = reloadUsers();
        }

        boolean userFound = existingUsers.stream()
                .anyMatch(user -> (user.equals(expenseUser)));

        if (userFound)
        {
            logger.info("Found matching user: " + expenseUser.getUsername());
            return true;
        }
        else
        {
            logger.info("Found no matching users...");
            if (useCache)
            {
                return validateUser(expenseUser, false);
            }
        }
        return false;
    }


    private ImmutableList<ExpenseUser> reloadUsers()
    {
        logger.info("Reloading Users...");
        try
        {
            myDatabaseView.openConnection();
            ImmutableList<ExpenseUser> expenseUsers = myDatabaseView.loadUsers();
            myDatabaseView.closeConnection();
            logger.info("Users reloaded successfully...");
            return expenseUsers;
        } catch (SQLException e)
        {
            logger.info("Failed to load users: " + e);
        }
        return ImmutableList.copyOf(Collections.emptyList());
    }

    public void cleanCache()
    {
        REQUEST_CACHE.cleanup();
    }

}
