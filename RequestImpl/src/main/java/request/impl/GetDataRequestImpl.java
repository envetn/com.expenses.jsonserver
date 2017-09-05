package request.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jsonserver.common.datatype.RequestId;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.Limit;
import jsonserver.common.datatype.Order;
import jsonserver.common.datatype.Validation.ValidationUtil;
import jsonserver.common.view.GetRequest;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by olof on 2016-09-15.
 */
public class GetDataRequestImpl implements GetRequest//, UserRequest
{
    @JsonProperty(value = "id")
    private final RequestId id;

    @JsonProperty(value = "requestDate")
    private final Date requestDate;

    @JsonProperty(value = "requestType")
    private final String requestType;

    @JsonProperty(value = "order")
    private final Order order;

    @JsonProperty(value = "limit")
    private final Limit limit;

    @JsonProperty(value = "user", required = true)
    private ExpenseUser user;

    public GetDataRequestImpl(RequestId id, String requestType, Order order, Date requestDate, Limit limit, ExpenseUser user) throws IOException
    {
        this.id = id;
        this.requestDate = requestDate;
        this.requestType = requestType;
        this.order = order;
        this.limit = limit;

        throwIfNull(user);
        this.user = user;
    }

    public GetDataRequestImpl()
    {
        this.id = null;
        this.requestDate = null;
        this.requestType = null;
        this.order = null;
        this.limit = null;
        this.user = null;
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
    public ExpenseUser getUser()
    {
        return user;
    }

    @Override
    public Order getOrder()
    {
        return order;
    }

    @Override
    public Limit getLimit()
    {
        return limit;
    }


    @Override
    public boolean isValid()
    {
        ValidationUtil validationUtil = new ValidationUtil();
        if (id.asString()
                .equals("Expenses"))
        {
            return validationUtil.validate(limit, false) && validationUtil.validate(order, true) && validationUtil.validate(user, false);
        }
        else
        {
            return validationUtil.validate(limit, false) && validationUtil.validate(order, true);
        }
    }

    @Override
    public String toString()
    {
        return "GetDataRequestImpl{" + "id=" + id + ", requestDate=" + requestDate + ", requestType='" + requestType + '\'' + ", order=" + order + ", limit=" + limit + ", user=" + user + '}';
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

        GetDataRequestImpl that = (GetDataRequestImpl) o;

        return id != null ? id.equals(that.id) : that.id == null && !(requestDate != null && that.getRequestDate() != null && f.format(requestDate)
                .equals(f.format(that.getRequestDate()))) && (requestType != null ? requestType.equals(that.requestType) : that.requestType == null && (order != null ? order.equals(that.order) : that.order == null && (limit != null ? limit.equals(that.limit) : that.limit == null)));

    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (requestDate != null ? requestDate.hashCode() : 0);
        result = 31 * result + (requestType != null ? requestType.hashCode() : 0);
        result = 31 * result + (order != null ? order.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        return result;
    }

    private void throwIfNull(ExpenseUser user) throws IOException
    {
        if (user == null || user.getUserId()
                .isEmpty() || user.getPassword()
                .isEmpty())
        {
            throw new IOException("Argument User can not be null");
        }
    }
}
