package jsonserver.common.datatype;

import java.sql.Date;

/**
 * Created by lofie on 2017-05-21.
 */
public class Temperature
{
    private final String myTemperature;
    private final Date myDate;
    private String myTime;
    private boolean mySpeculated;

    public Temperature(Date date, String temperature, String time, boolean speculated)
    {
        myDate = date;
        myTemperature = temperature;
        myTime = time;
        mySpeculated = speculated;

    }

    public boolean isSpeculated()
    {
        return mySpeculated;
    }

    public void setSpeculated(boolean speculated)
    {
        mySpeculated = speculated;
    }

    public Date getDate()
    {
        return myDate;
    }

    public String getTemperature()
    {
        return myTemperature;
    }

    public String getTime()
    {
        return myTime;
    }
}
