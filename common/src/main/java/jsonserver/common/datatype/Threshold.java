package jsonserver.common.datatype;

import com.google.gson.JsonObject;

/**
 * Created by lofie on 2017-05-21.
 */
public class Threshold
{
        final int myCurrentValue;
        final int myThresholdValue;
        final String myType;
        final int myMonth;

        public Threshold(int currentValue, int thresholdValue, int month, String type)
        {
            myCurrentValue = currentValue;
            myThresholdValue = thresholdValue;
            myType = type;
            myMonth = month;
        }

        private boolean evaluate()
        {
            return myCurrentValue > myThresholdValue;
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
}
