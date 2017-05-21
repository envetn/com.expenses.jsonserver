package operations;

import com.google.gson.JsonObject;
import jsonserver.common.json.JsonResponseHandler;
import jsonserver.common.view.Expense.ExpensePutRequest;

import java.sql.SQLException;

/**
 *
 */
public class ExpensesWriteOperation extends DatabaseOperation<ExpensePutRequest>
{
    private static final String OPERATION = "Expenses Write";

    public ExpensesWriteOperation()
    {
       super(OPERATION);
    }

    @Override
    protected JsonObject performOperation(ExpensePutRequest expenseRequest) throws SQLException
    {
        int sqlResult = myDatabaseConnection.insertExpensesValues(expenseRequest);

        return JsonResponseHandler.createDefaultResponse(sqlResult, expenseRequest, "Put");
    }
}
