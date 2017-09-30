package jsonserver.common.containers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jsonserver.common.datatype.Fetchperiod;
import jsonserver.common.datatype.Limit;
import jsonserver.common.datatype.Temperature;
import jsonserver.common.view.GetRequest;
import jsonserver.common.view.Temperature.TemperaturePutRequest;

import java.sql.Date;
import java.util.List;

import static jsonserver.common.Utils.DateUtils.getCurrentTimestamp;


/**
 * Created by lofie on 2017-07-15.
 */
public class TemperatureContainer
{
    private final List<Temperature> myTemperature;
    private Temperature speculatedTemperature;

    public TemperatureContainer(List<Temperature> temperature)
    {
        myTemperature = temperature;
    }

    public List<Temperature> getTemperatures()
    {
        return myTemperature;
    }

    public Temperature getSpeculatedTemperature()
    {
        return speculatedTemperature;
    }

    public JsonObject putInto(TemperaturePutRequest putRequest)
    {
        String requestTime = putRequest.getRequestTime();
        String temperature1 = putRequest.getTemperature();
        Date requestDate = putRequest.getRequestDate();

        speculatedTemperature = new Temperature(requestDate, temperature1, requestTime, true);

        myTemperature.add(speculatedTemperature);
        JsonObject jsonResponse = getJsonHeader("PUT", "Put-Temperature");

        JsonObject object = createJsonObject(speculatedTemperature);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(object);
        jsonResponse.add("Get-Data", jsonArray);

        return jsonResponse;
    }

    public JsonObject readTemperature(GetRequest getRequest)
    {
        Limit limit = getRequest.getLimit();

        Fetchperiod fetchperiod = limit.getFetchperiod();
        String timeStart = fetchperiod.getTimeStart();
        String timeEnd = fetchperiod.getTimeEnd();

        Long start = Date.valueOf(timeStart).getTime();
        Long end = Date.valueOf(timeEnd).getTime();

        JsonObject jsonResponse = getJsonHeader("GET", "Get-Temperature");

        JsonArray jsonArray = new JsonArray();
        for (Temperature temperature : myTemperature)
        {
            Date date = temperature.getDate();

            Long current = date.getTime();

            if (start <= current && current <= end)
            {
                JsonObject object = createJsonObject(temperature);
                //UUID - ??
                jsonArray.add(object);
            }
        }

        jsonResponse.add("Get-Data", jsonArray);

        return jsonResponse;
    }

    private JsonObject createJsonObject(Temperature temperature)
    {
        JsonObject object = new JsonObject();
        object.addProperty("date", temperature.getDate().toString());
        object.addProperty("temperature", temperature.getTemperature());
        object.addProperty("time", temperature.getTime());
        return object;
    }

    private JsonObject getJsonHeader(String type, String reason)
    {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("Response", "Success");
        jsonResponse.addProperty("Time", getCurrentTimestamp());
        jsonResponse.addProperty("where", "Temperature Response");
        jsonResponse.addProperty("Reason", reason);
        jsonResponse.addProperty("Type", type);
        return jsonResponse;
    }
}
