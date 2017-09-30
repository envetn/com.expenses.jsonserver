package request.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jsonserver.common.datatype.RequestId;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.view.Request;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Foten on 11/30/2016.
 */
public class CreateUserRequest implements Request
{
    @JsonProperty(value = "id")
    private final RequestId id;

    @JsonProperty(value = "requestDate")
    private final Date requestDate;

    @JsonProperty(value = "requestType")
    private final String requestType;

    @JsonProperty(value = "user", required = true)
    private final ExpenseUser user;

    //Dummy
    public CreateUserRequest()
    {
        this.id = null;
        this.requestDate = null;
        this.requestType = null;
        this.user = null;
    }

    public CreateUserRequest(RequestId id, Date date, String requestType, ExpenseUser user)
    {
        this.id = id;
        this.requestDate = date;
        this.requestType = requestType;
        this.user = user;
    }


    @Override
    public ExpenseUser getUser()
    {
        return user;
    }

    @Override
    public RequestId getId()
    {
        return id;
    }

    @Override
    public Date getRequestDate()
    {
        return requestDate;
    }

    @Override
    public String getRequestType()
    {
        return requestType;
    }


    @Override
    public boolean isValid()
    {
        return user != null && user.validate();
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

        CreateUserRequest that = (CreateUserRequest) o;

        return id != null ? id.equals(that.id) : that.id == null && !(requestDate != null && that.getRequestDate() != null && f.format(requestDate)
                .equals(f.format(that.getRequestDate()))) && (requestType != null ? requestType.equals(that.requestType) : that.requestType == null && that.user == null);
    }

    @Override
    public String toString()
    {
        return "CreateUserRequest{" + "id=" + id + ", requestDate=" + requestDate + ", requestType='" + requestType + '\'' + ", user=" + user + '}';
    }


    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (requestDate != null ? requestDate.hashCode() : 0);
        result = 31 * result + (requestType != null ? requestType.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
