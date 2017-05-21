package Connection;

import jsonserver.common.datatype.RequestId;
import jsonserver.common.datatype.RequestId.ValidRequestIdEnum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static jsonserver.common.datatype.RequestId.ValidRequestIdEnum.*;

/**
 * Created by Foten on 4/21/2017.
 */
public enum DatabaseTables
{

    EXPENSES_TABLE("expenses"),
    TEMPERATURE_TABLE("temperature"),
    USER_TABLE("expenseuser"),
    THRESHOLD_TABLE("threshold"),
    DUMMY_TABLE("Dummy");

    private static final Map<RequestId, DatabaseTables> TABLES;

    static
    {
        Map<RequestId, DatabaseTables> tempTables = new HashMap<>();

        tempTables.put(USER.getId(), USER_TABLE);
        tempTables.put(EXPENSES.getId(), EXPENSES_TABLE);
        tempTables.put(TEMPERATURE.getId(), TEMPERATURE_TABLE);
        tempTables.put(THRESHOLD.getId(), THRESHOLD_TABLE);

        TABLES = Collections.unmodifiableMap(tempTables);
    }

    private final String myTableName;

    DatabaseTables(String tableName)
    {
        myTableName = tableName;
    }

    public static DatabaseTables fetchDatabaseTable(RequestId requestId)
    {
        DatabaseTables databaseTables = TABLES.get(requestId);
        if(databaseTables == null)
        {
            return DUMMY_TABLE;
        }
        return databaseTables;
    }
    public String getTable()
    {
        return myTableName;
    }
}
