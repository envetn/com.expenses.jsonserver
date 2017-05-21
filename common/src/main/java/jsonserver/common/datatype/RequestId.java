package jsonserver.common.datatype;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by olof on 2016-07-09.
 */
public class RequestId
{
    @JsonProperty (value = "requestId")
    private String requestId;

    public RequestId()
    {
    }

    public RequestId(String id)
    {
        requestId = id;
    }

    public String asString()
    {
        return requestId;
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

        RequestId requestId1 = (RequestId) o;

        return requestId != null ? requestId.equals(requestId1.requestId) : requestId1.requestId == null;

    }

    @Override
    public int hashCode()
    {
        return requestId != null ? requestId.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return " [ " + requestId + " ]";
    }

    public enum ValidRequestIdEnum
    {
        USER("User"),
        EXPENSES("Expenses"),
        TEMPERATURE("Temperature"),
        THRESHOLD("Threshold");


        private final RequestId myEnumRequestId;
        ValidRequestIdEnum(String id)
        {
            myEnumRequestId  = new RequestId(id);
        }

        public RequestId getId()
        {
            return myEnumRequestId;
        }
    }
}
