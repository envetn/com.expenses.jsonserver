package request.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.RequestId;
import jsonserver.common.datatype.Validation.ValidationUtil;
import jsonserver.common.view.Temperature.TemperaturePutRequest;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class TemperaturePutRequestImpl implements TemperaturePutRequest
{
    public static final String TYPE = "Temperature";

    @JsonProperty(value = "id")
    private RequestId requestId;

    @JsonProperty(value = "time")
    private String requestTime;

    @JsonProperty(value = "requestDate")
    private final Date requestDate;

    @JsonProperty(value = "temperature")
    private String myTemperature;

    @JsonProperty(value = "requestType")
    private final String myRequestType;

    @JsonProperty(value = "user")
    private final ExpenseUser myUser;

    public TemperaturePutRequestImpl()
    {
        requestId = null;
        requestTime = null;
        requestDate = null;
        myTemperature = null;
        myRequestType = null;
        myUser = null;
    }

    public TemperaturePutRequestImpl(RequestId id, String time, Date date, String temperature, String requestType, ExpenseUser user)
    {
        requestId = id;
        requestTime = time;
        requestDate = date;
        myTemperature = temperature;
        myRequestType = requestType;
        myUser = user;
    }

    @Override
    public RequestId getId()
    {
        return requestId;
    }

    @Override
    public String getRequestTime()
    {
        return requestTime;
    }

    @Override
    public Date getRequestDate()
    {
        return requestDate;
    }

    @Override
    public ExpenseUser getUser()
    {
        return myUser;
    }

    @Override
    public String getTemperature()
    {
        return myTemperature;
    }

    @JsonIgnore
    @Override
    public boolean isValid()
    {
        return ValidationUtil.isNull(requestId, requestTime, requestDate, myTemperature, myRequestType);
    }

    @Override
    public String toString()
    {
        return "TemperaturePutRequestImpl{" + "requestId=" + requestId + ", requestTime='" + requestTime + '\'' + ", requestDate=" + requestDate + ", myTemperature='" + myTemperature + '\'' + ", myRequestType='" + myRequestType + '\'' + '}';
    }

    @Override
    public boolean equals(Object o)
    {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        TemperaturePutRequestImpl that = (TemperaturePutRequestImpl) o;

        return Objects.equals(requestId, that.requestId) && Objects.equals(requestTime, that.requestTime) && Objects.equals(f.format(requestDate), f.format(that.requestDate)) && Objects.equals(myTemperature, that.myTemperature) && Objects.equals(myRequestType, that.myRequestType);
    }

    @Override
    public int hashCode()
    {
        int result = requestId != null ? requestId.hashCode() : 0;
        result = 31 * result + (requestDate != null ? requestDate.hashCode() : 0);
        result = 31 * result + (requestTime != null ? requestTime.hashCode() : 0);
        result = 31 * result + (myTemperature != null ? myTemperature.hashCode() : 0);
        result = 31 * result + (myRequestType != null ? myRequestType.hashCode() : 0);
        return result;
    }

}
