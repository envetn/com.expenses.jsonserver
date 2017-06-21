package Connection;

import com.google.gson.JsonObject;
import jsonserver.common.datatype.Expenses;
import jsonserver.common.datatype.Limit;
import jsonserver.common.datatype.Order;
import jsonserver.common.datatype.Threshold;
import jsonserver.common.view.GetRequest;

import java.sql.Date;
import java.util.List;

/**
 * Created by lofie on 2017-05-23.
 */
public class ExpensesDbConnection
{
    private final GetRequest myRequest;
    private final List<Expenses> myExpenses;
    private final List<Threshold> myThresholds;


    public ExpensesDbConnection(GetRequest myRequest, List<Expenses> myExpenses, List<Threshold> myThresholds)
    {
        this.myRequest = myRequest;
        this.myExpenses = myExpenses;
        this.myThresholds = myThresholds;
    }

    public void createExpensesResponse()
    {

        Limit limit = myRequest.getLimit();
        Date timeStart = Date.valueOf(limit.getFetchperiod().getTimeStart());
        Date timeEnd = Date.valueOf(limit.getFetchperiod().getTimeEnd());


        Order order = myRequest.getOrder();

        for (Expenses myExpens : myExpenses)
        {
            Date buyDate = myExpens.getBuyDate();

            if(timeStart.after(buyDate) && timeEnd.before(buyDate))
            {
                JsonObject object = new JsonObject();
                object.addProperty("cost", myExpens.getCost());
                object.addProperty("costType", myExpens.getCostType());
                object.addProperty("buyDate", myExpens.getBuyDate().toString());
                object.addProperty("comment", myExpens.getComment());
                object.addProperty("uuid", myExpens.getUuid());
            }
        }
    }
}
