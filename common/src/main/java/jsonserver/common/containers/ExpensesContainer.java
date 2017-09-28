package jsonserver.common.containers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jsonserver.common.datatype.*;
import jsonserver.common.view.DeleteRequest;
import jsonserver.common.view.Expense.ExpensePutRequest;
import jsonserver.common.view.GetRequest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static jsonserver.common.Utils.DateUtils.getCurrentTimestamp;

/**
 * Created by lofie on 2017-06-24.
 */
public class ExpensesContainer
{
    private final List<Threshold> myThresholds;
    private final List<Expenses> myExpenses;

    private final List<Expenses> myListOfSpeculatedExpenses = new ArrayList<>();
    private final List<Threshold> myListOfSpeculatedThreshold = new ArrayList<>();

    public ExpensesContainer(List<Expenses> expenses, List<Threshold> thesholds)
    {
        myExpenses = expenses;
        myThresholds = thesholds;
    }

    public void clearSpecualted()
    {
        myListOfSpeculatedExpenses.clear();
        myListOfSpeculatedThreshold.clear();
    }

    public List<Expenses> getExpenses()
    {
        return myExpenses;
    }

    public Threshold getFirstSpeculatedThreshold()
    {
        if (myListOfSpeculatedThreshold.isEmpty())
        {
            return null;
        }

        return myListOfSpeculatedThreshold.get(0);
    }


    public Expenses getFirstSpeculatedExpense()
    {
        if (myListOfSpeculatedExpenses.isEmpty())
        {
            return null;
        }

        return myListOfSpeculatedExpenses.get(0);
    }

    public List<Expenses> getListOfspeculatedExpenses()
    {
        return myListOfSpeculatedExpenses;
    }

    public List<Threshold> getListOfSpeculatedThresholds()
    {
        return myListOfSpeculatedThreshold;
    }

    public List<Threshold> getThresholds()
    {
        return myThresholds;
    }

    public JsonObject insertInto(ExpensePutRequest putRequest)
    {
        Content content = putRequest.getContent();

        Expenses expenses = new Expenses(content.getCost(), content.getCostType(), content.getbuyDate(), content.getComment(), content.getUUID(), false);
        myExpenses.add(expenses);
        myListOfSpeculatedExpenses.add(expenses);

        //TODO, if no table with threshold values, what to do?
        JsonObject thresholdJsonObject = updateThresholdValues(putRequest, expenses);

        JsonObject expensesHeader = getExpensesHeader("Put-Expenses", "PUT");

        JsonObject object = createExpensesJsonObject(expenses);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(object);

        expensesHeader.add("Get-Data", object);
        expensesHeader.add("ThresholdResult", thresholdJsonObject);
        return expensesHeader;

    }

    private JsonObject updateThresholdValues(ExpensePutRequest putRequest, Expenses expenses)
    {
        ExpenseUser user = putRequest.getUser();
        for (Threshold threshold : myThresholds)
        {
            if (threshold.isCorrectThreshold(user.getUsername(), expenses))
            {
                threshold.updateThreshold(expenses, "Add");
                myListOfSpeculatedThreshold.add(threshold);

                return threshold.createObject();
            }
        }
        return new JsonObject();
    }

    public JsonObject createExpensesJsonObject(GetRequest getRequest)
    {
        Limit limit = getRequest.getLimit();
        Date timeStart;
        Date timeEnd;

        timeStart = Date.valueOf(limit.getFetchperiod().getTimeStart());
        timeEnd = Date.valueOf(limit.getFetchperiod().getTimeEnd());

        JsonObject jsonResponse = getExpensesHeader("Get-Expenses", "GET");

        //right now only fetch by day with no order
        JsonArray content = new JsonArray();
        for (Expenses expenses : myExpenses)
        {
            Date buyDate = expenses.getBuyDate();

            Long start = timeStart.getTime();
            Long end = timeEnd.getTime();
            Long current = buyDate.getTime();

            if (start <= current && current <= end)
            {
                JsonObject object = createExpensesJsonObject(expenses);
                content.add(object);
            }
        }
        System.out.println("Size of array: " + content.size());
        jsonResponse.add("Get-Data", content);
        jsonResponse.add("ThresholdResult", getThresholdValues());

        return jsonResponse;
    }

    private JsonElement getThresholdValues()
    {
        JsonArray jsonArray = new JsonArray();

        for (Threshold threshold : myThresholds)
        {
            jsonArray.add(threshold.createObject());
        }

        return jsonArray;
    }

    private JsonObject getExpensesHeader(String reason, String type)
    {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("Response", "Success");
        jsonResponse.addProperty("Time", getCurrentTimestamp());
        jsonResponse.addProperty("where", "Expenses Response");
        jsonResponse.addProperty("Reason", type);
        jsonResponse.addProperty("Type", reason);
        return jsonResponse;
    }

    private JsonObject createExpensesJsonObject(Expenses expenses)
    {
        JsonObject object = new JsonObject();
        object.addProperty("cost", expenses.getCost());
        object.addProperty("costType", expenses.getCostType());
        object.addProperty("buyDate", expenses.getBuyDate().toString());
        object.addProperty("comment", expenses.getComment());
        object.addProperty("uuid", expenses.getUuid());
        return object;
    }

    public JsonObject removeFrom(DeleteRequest request)
    {
        //TODO: maybe add the content of what was removed?
        List<String> idsToRemove = request.getIdToRemove();

        //could this be done in a better way?

        for (String idToRemove : idsToRemove)
        {
            for (Expenses expenses : myExpenses)
            {
                if (idToRemove.equals(expenses.getUuid()))
                {
                    myListOfSpeculatedExpenses.add(expenses);
                    myExpenses.remove(expenses);
                    break;
                }
            }
        }

        JsonArray jsonArray = new JsonArray();
        for (Expenses speculatedExpens : myListOfSpeculatedExpenses)
        {
            JsonObject jsonObject = updateThresholdValuesRemove(request.getUser(), speculatedExpens);
            jsonArray.add(jsonObject);
        }
        JsonObject jsonHeader = getExpensesHeader("Remove-Expense", "REMOVE");
        jsonHeader.addProperty("AffectedRows", myListOfSpeculatedExpenses.size());
        jsonHeader.add("ThresholdResult", jsonArray);

        return jsonHeader;
    }

    private JsonObject updateThresholdValuesRemove(ExpenseUser user, Expenses expenses)
    {
        for (Threshold threshold : myThresholds)
        {
            if (threshold.isCorrectThreshold(user.getUsername(), expenses))
            {
                threshold.updateThreshold(expenses, "Subtract");

                myListOfSpeculatedThreshold.add(threshold);

                return threshold.createObject();
            }
        }
        return new JsonObject();
    }
}
