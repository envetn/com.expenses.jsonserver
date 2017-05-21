package jsonserver.common.datatype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jsonserver.common.datatype.Validation.Validation;

import java.util.Objects;

/**
 * Created by olof on 2016-09-15.
 */
public class Fetchperiod implements Validation
{
    public static final String MONTH = "month";
    public static final String ALL = "All";
    public static final String DAY = "day";
    public static final String TIME_PERIOD = "timePeriod";

    public static final String DATE_REGEX = "[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]";


    @JsonProperty (value = "period")
    private String period;
    @JsonProperty (value = "periodToFetch")
    private String periodToFetch;
    @JsonProperty (value = "hasPeriod")
    private boolean hasPeriod = false;
    @JsonProperty (value = "timeStart")
    private String timeStart;
    @JsonProperty (value = "timeEnd")
    private String timeEnd;

    public Fetchperiod()
    {
    }

    public Fetchperiod(boolean hasPeriod, String periodToFetch, String period, String timeStart, String timeEnd)
    {
        this.hasPeriod = hasPeriod;
        this.period = period;
        this.periodToFetch = periodToFetch;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public String getPeriodToFetch()
    {
        return periodToFetch;
    }

    public String getPeriod()
    {
        return period;
    }

    public boolean hasPeriod()
    {
        return hasPeriod;
    }

    public String getTimeStart()
    {
        return timeStart;
    }

    public String getTimeEnd()
    {
        return timeEnd;
    }

    @Override
    public String toString()
    {
        return "   Period: " + period + ", hasPeriod: " + hasPeriod + ", periodToFetch: " + periodToFetch + ", timeStart: " + timeStart + ", timeEnd: " + timeEnd + "\n";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Fetchperiod that = (Fetchperiod) o;

        return hasPeriod == that.hasPeriod &&
                Objects.equals(periodToFetch, that.periodToFetch) &&
                Objects.equals(timeStart, that.timeStart) &&
                Objects.equals(timeEnd, that.timeEnd);


    }

    @Override
    public int hashCode()
    {
        int result = periodToFetch != null ? periodToFetch.hashCode() : 0;
        result = 31 * result + (hasPeriod ? 1 : 0);
        return result;
    }

    @JsonIgnore
    @Override
    public boolean validate()
    {
        if(period == null || period.isEmpty())
        {
            if(periodToFetch == null || periodToFetch.isEmpty())
            {
                return true; // no period
            }
        }
        else
        {
            //Has period. Period should be either month/All or day
            if(period.equals("All") ||
                    period.equals("month") ||
                    period.equals("day") ||
                    period.equals(TIME_PERIOD))
            {
                return validatePeriod();
            }
        }

        return false;
    }

    private boolean validatePeriod()
    {
        switch (period)
        {
            case "All":
                return true;
            case "month":
                Integer monthNumber = periodToFetch.length() > 0 ? Integer.valueOf(periodToFetch) : -1;
                return monthNumber > 0 && monthNumber < 13;
            case "day":
                return periodToFetch.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]");
            case TIME_PERIOD:
                return timeStart.matches(DATE_REGEX) && timeEnd.matches(DATE_REGEX);

            default:
                return false;
        }
    }
}
