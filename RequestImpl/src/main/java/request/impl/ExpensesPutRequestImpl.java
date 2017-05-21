package request.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jsonserver.common.datatype.RequestId;
import jsonserver.common.datatype.Content;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.view.Expense.ExpensePutRequest;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Created by olof on 2016-09-17.
 */
public class ExpensesPutRequestImpl implements ExpensePutRequest//, UserRequest
{
    @JsonProperty(value = "id")
    private final RequestId id;

    @JsonProperty(value = "requestDate")
    private final Date requestDate;

    @JsonProperty(value = "requestType")
    private final String requestType;

    @JsonProperty(value = "content")
    private final Content content;

    @JsonProperty(value = "user")
    private ExpenseUser user;

    //Dummy
    public ExpensesPutRequestImpl()
    {
        this.id = null;
        this.requestDate = null;
        this.requestType = null;
        this.content = null;
        this.user = null;
    }

    public ExpensesPutRequestImpl(RequestId id, Date date, String requestType, Content content, ExpenseUser user)
    {
        this.id = id;
        this.requestDate = date;
        this.requestType = requestType;
        this.content = content;
        this.user = user;
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
    public Content getContent()
    {
        return content;
    }


    @Override
    public boolean isValid()
    {
        return true;
    }

    @Override
    public ExpenseUser getUser()
    {
        return user;
    }

    @Override
    public String toString()
    {
        return "ExpensesPutRequestImpl{" + "id=" + id + ", requestDate=" + requestDate + ", requestType='" + requestType + '\'' + ", content=" + content + ", user=" + user + '}';
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

        ExpensesPutRequestImpl that = (ExpensesPutRequestImpl) o;

        return Objects.equals(id, that.id) && Objects.equals(f.format(requestDate), f.format(that.getRequestDate())) && Objects.equals(requestType, that.requestType) && Objects.equals(content, that.content);
//                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (requestDate != null ? requestDate.hashCode() : 0);
        result = 31 * result + (requestType != null ? requestType.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
