package jsonserver.common.datatype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jsonserver.common.datatype.Validation.Validation;

import java.sql.Date;

/**
 * Created by olof on 2016-09-18.
 *
 * Stores data content of Put request
 */
public class Content implements Validation
{
    @JsonProperty (value = "cost")
    private final String cost;

    @JsonProperty (value = "comment")
    private final String comment;

    @JsonProperty (value = "costType")
    private final String costType;

    @JsonProperty (value = "buyDate")
    private final Date buyDate;

    @JsonProperty (value = "uuid")
    private final String uuid;

    public Content()
    {
        buyDate = null;
        cost = null;
        comment = null;
        costType = null;
        uuid = null;
    }

    public Content(String cost, String comment, String costType, Date buyDate, String uuid)
    {
        this.cost = cost;
        this.comment = comment;
        this.costType = costType;
        this.buyDate = buyDate;
        this.uuid = uuid;
    }

    public Date getbuyDate()
    {
        return buyDate;
    }

    public String getCost()
    {
        return cost;
    }

    public String getComment()
    {
        return comment;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getCostType()
    {
        return costType;
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

        Content content = (Content) o;

        if (cost != null ? !cost.equals(content.cost) : content.cost != null)
        {
            return false;
        }
        if (comment != null ? !comment.equals(content.comment) : content.comment != null)
        {
            return false;
        }
        if (costType != null ? !costType.equals(content.costType) : content.costType != null)
        {
            return false;
        }
        return uuid != null ? uuid.equals(content.uuid) : content.uuid == null;
    }

    @Override
    public int hashCode()
    {
        int result = cost != null ? cost.hashCode() : 0;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (costType != null ? costType.hashCode() : 0);
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }

    @JsonIgnore
    @Override
    public boolean validate()
    {
        return true;
    }
}
