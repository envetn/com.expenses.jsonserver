package operations;

import Connection.DatabaseConnection;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import jsonserver.common.datatype.RequestId;
import jsonserver.common.json.JsonResponseHandler;
import jsonserver.common.view.Expense.ExpensePutRequest;
import jsonserver.common.view.Request;
import jsonserver.common.view.Temperature.TemperaturePutRequest;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Foten on 4/22/2017.
 */
public class WriteOperation extends DatabaseOperation<Request>
{
    private final WriteOperationsEnum myOperation;

    public WriteOperation(Request request)
    {
        super("Write" + request.getId());
        RequestId requestId = request.getId();
        myOperation = WriteOperationsEnum.getOperation(requestId.asString());
    }

    @Override
    protected JsonObject performOperation(Request request) throws SQLException
    {
        int sqlResult = myOperation.executeWrite(myDatabaseConnection, request);
        return JsonResponseHandler.createDefaultResponse(sqlResult, request, "Put");
    }

    public enum WriteOperationsEnum
    {
        USER("User")
                {
                    @Override
                    public int executeWrite(DatabaseConnection databaseConnection, Request request) throws SQLException
                    {
                        return databaseConnection.createUser(request);
                    }
                },
        TEMPERATURE("Temperature")
                {
                    @Override
                    public int executeWrite(DatabaseConnection databaseConnection, Request request) throws SQLException
                    {
                        return databaseConnection.insertTemperatureValues((TemperaturePutRequest) request);
                    }
                },
        EXPENSES("Expenses")
                {
                    @Override
                    public int executeWrite(DatabaseConnection databaseConnection, Request request) throws SQLException
                    {
                        return databaseConnection.insertExpensesValues((ExpensePutRequest) request);
                    }
                },;

        private static final Map<String, WriteOperationsEnum> REQUEST_CREATOR_MAP;

        static
        {
            Map<String, WriteOperationsEnum> myTemporaryMap = new HashMap<>();

            for (WriteOperationsEnum jsonCreator : WriteOperationsEnum.values())
            {
                myTemporaryMap.put(jsonCreator.myId, jsonCreator);
            }

            REQUEST_CREATOR_MAP = ImmutableMap.copyOf(myTemporaryMap);
        }

        private final String myId;

        public abstract int executeWrite(DatabaseConnection databaseConnection, Request request) throws SQLException;


        WriteOperationsEnum(String id)
        {
            myId = id;
        }

        public static WriteOperationsEnum getOperation(String id)
        {
            return REQUEST_CREATOR_MAP.get(id);
        }
    }
}
