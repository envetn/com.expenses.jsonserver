package jsonserver.common.Utils;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
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
 * Created by lofie on 2017-07-16.
 */
public class DateUtils
{
    public static String getCurrentTimestamp()
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
}
