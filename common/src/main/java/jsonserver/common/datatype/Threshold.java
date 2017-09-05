package jsonserver.common.datatype;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.util.Calendar;

/**
 * Created by lofie on 2017-05-21.
 */
public class Threshold
{

    private static final Logger LOGGER = Logger.getLogger(Threshold.class);
    private int myCurrentValue;
    private final int myThresholdValue;
    private final String myType;
    private final int myMonth;
    private final String myUsername;
    private boolean myHasBeenUpdated = false;

    public Threshold(int currentValue, int thresholdValue, int month, String type, String username)
    {
        myCurrentValue = currentValue;
        myThresholdValue = thresholdValue;
        myType = type;
        myMonth = month;
        myUsername = username;
    }

    public int getCurrentValue()
    {
        return myCurrentValue;
    }
    private boolean evaluate()
    {
        return myCurrentValue > myThresholdValue;
    }

    public boolean isCorrectThreshold(String userName, Expenses expenses)
    {
        String type = expenses.getCostType();
        Date date = expenses.getBuyDate();

        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        int month = instance.get(Calendar.MONTH);
        LOGGER.info("Validating if threshold is valid\n " + myUsername + " == " + userName + "\n " + myMonth + " == " + month);

        return myUsername.equals(userName)
                && myType.equals(type)
                && myMonth == month;

    }

    public void updateThreshold(Expenses expenses, String operation)
    {
        //Todo: If expense is removed, threshold need to be updated with the new value
        String cost = expenses.getCost();
        int expensesCost = Integer.valueOf(cost);

        myHasBeenUpdated = true;

        if(operation.equals("Add"))
        {
            myCurrentValue = myCurrentValue + expensesCost;
        }
        else
        {
            myCurrentValue = myCurrentValue - expensesCost;
        }

    }

    public boolean hasBeenUpdated()
    {
        return myHasBeenUpdated;
    }


    public JsonObject createObject()
    {
        JsonObject thresholdObject = new JsonObject();
        thresholdObject.addProperty("month", myMonth);
        thresholdObject.addProperty("type", myType);

        thresholdObject.addProperty("thresholdValue", myThresholdValue);
        thresholdObject.addProperty("currentValue", myCurrentValue);
        thresholdObject.addProperty("hasPassed", evaluate());
        return thresholdObject;
    }

    public String getType()
    {

        return myType;
    }

    public int getMonth()
    {
        return myMonth;
    }

}
