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

    public static String getTimestamp()
    {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return dateFormat.format(calender.getTime());
    }
    public static int getMonthNumber(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static Date getFirstDayOfMonthInSql()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        java.util.Date utilDate = cal.getTime();

        return new Date(utilDate.getTime());
    }

    public static Date getLastDayOfMonth()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        java.util.Date utilDate = cal.getTime();

        return new Date(utilDate.getTime());
    }

    public static Map<String, String> getFirstAndLastDayOf(int givenMonth)
    {
        LocalDateTime now = LocalDateTime
                .now()
                .withMonth(givenMonth);

        Map<String, String> minMax = new HashMap<>();
        int month = now.getMonthValue();
        int year = now.getYear();

        LocalDate initial = LocalDate.of(year, month, 1);
        LocalDate start = initial.with(firstDayOfMonth());
        LocalDate end = initial.with(lastDayOfMonth());

        minMax.put("first", start.toString());
        minMax.put("last", end.toString());

        return  minMax;
    }

    @Deprecated
    public static Map<String, Date> getFirstAndLastDayOf(String givenMonth) throws IOException
    {
        DateFormatSymbols dfs = new DateFormatSymbols();

        String[] months = dfs.getShortMonths();

        for(int i=0; i<months.length; i++)
        {
            String month = months[i].replace(".", "");
            if(month.equalsIgnoreCase("Oct"))
            {
                month = "Okt";
            }
            if(month.equalsIgnoreCase(givenMonth))
            {
                return stuff(i);
            }
        }

        throw new IOException("Could not find month matching: " + givenMonth);
    }

    private static Map<String, Date> stuff(int month)
    {
        Map<String, Date> minMax = new HashMap<>();

        int[] daysInAMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        int days = daysInAMonth[month];

        GregorianCalendar calendarLastDay = new GregorianCalendar(2016, month, days);
        GregorianCalendar calendarFirstDay = new GregorianCalendar(2016, month, 1);
        java.util.Date monthEndDate = new java.util.Date(calendarLastDay.getTime().getTime());
        java.util.Date monthStartDay = new java.util.Date(calendarFirstDay.getTime().getTime());
        minMax.put("first", new Date(monthStartDay.getTime()));
        minMax.put("last", new Date(monthEndDate.getTime()));
        return minMax;
    }
}

