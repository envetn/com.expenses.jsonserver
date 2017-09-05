package jsonserver.common.Utils;

import com.google.gson.*;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * Created by olof on 2016-07-09.
 */
public class Utilities
{
    private Utilities()
    {
    }

    public static final String DATE_KEY = "requestDate";
    public static final String TEMPERATURE_KEY = "temperature";
    public static final String TIME_KEY = "time";
    public static final String REQUEST_ID = "requestId";
    public static final String REQUEST_TYPE = "requestType";
    public static final int SQL_SUCCESS = 1;


    public static void print(String message)
    {
        System.out.println(message);
    }

    public static String getPrettyJsonString(JsonObject jsonObject)
    {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(jsonObject.toString());
        return gson.toJson(je);
    }


}

