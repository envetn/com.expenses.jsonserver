package jsonserver.common.datatype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jsonserver.common.datatype.Validation.Validation;

/**
 * Created by olof on 2016-09-17.
 */
public class Order implements Validation
{
    @JsonProperty (value = "orderBy")
    private final String orderBy;
    @JsonProperty (value = "isAscending")
    private final boolean isAscending;

    //Dummy
    public Order()
    {
        isAscending = false;
        orderBy = null;
    }

    public Order(String orderBy, boolean isAscending)
    {
        this.orderBy = orderBy;
        this.isAscending = isAscending;
    }

    public String getOrderBy()
    {
        return orderBy;
    }

    public boolean getIsAscending()
    {
        return isAscending;
    }

    @JsonIgnore
    public String getWhichOrder()
    {
        String whichOrder = isAscending ? " ASC" : " DESC";

        if(orderBy != null)
        {
            return orderBy + whichOrder;

        }

        return whichOrder;
    }

    @Override
    public String toString()
    {
        return "Order{" + "orderBy='" + orderBy + '\'' + ", isAscending=" + isAscending + '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Order order = (Order) o;

        if (isAscending != order.isAscending)
            return false;
        return orderBy != null ? orderBy.equals(order.orderBy) : order.orderBy == null;

    }

    @Override
    public int hashCode()
    {
        int result = orderBy != null ? orderBy.hashCode() : 0;
        result = 31 * result + (isAscending ? 1 : 0);
        return result;
    }

    @JsonIgnore
    @Override
    public boolean validate()
    {
        return true;
    }
}
