package request.impl;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import operations.RemoveOperation;
import jsonserver.common.datatype.RequestId;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.view.DeleteRequest;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by olof on 2016-07-06.
 */
public class DeleteRequestImpl implements DeleteRequest//, UserRequest
{
    @JsonProperty(value = "id")
    private final RequestId id;

    @JsonProperty(value = "requestDate")
    private final Date requestDate;

    @JsonProperty(value = "requestType")
    private final String requestType;

    @JsonProperty(value = "remove-Data")
    private final List<String> idToRemove;

    @JsonProperty(value = "user")
    private final ExpenseUser user;

    //Dummy
    public DeleteRequestImpl()
    {
        this.id = null;
        this.requestDate = null;
        this.requestType = null;
        this.idToRemove = new ArrayList<>();
        this.user = null;
    }

    public DeleteRequestImpl(RequestId id, Date requestDate, String requestType, List<String> idToRemove, ExpenseUser user)
    {
        this.id = id;
        this.requestDate = requestDate;
        this.requestType = requestType;
        this.idToRemove = idToRemove;
        this.user = user;
    }

    @Override
    public List<String> getIdToRemove()
    {
        return idToRemove;
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

    public ExpenseUser getUser()
    {
        return user;
    }

    @Override
    public boolean isValid()
    {
        return true;
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

        DeleteRequestImpl that = (DeleteRequestImpl) o;

        return Objects.equals(id, that.id) && Objects.equals(f.format(requestDate), f.format(that.requestDate)) && Objects.equals(requestType, that.requestType) && Objects.equals(idToRemove, that.idToRemove) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (requestDate != null ? requestDate.hashCode() : 0);
        result = 31 * result + (requestType != null ? requestType.hashCode() : 0);
        result = 31 * result + (idToRemove != null ? idToRemove.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
