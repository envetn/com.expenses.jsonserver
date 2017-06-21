package jsonserver.common.view;

import com.google.gson.JsonObject;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.RequestId;

import java.sql.Date;

/**
 * Created by olof on 2016-07-09.
 */
public interface Request
{
    RequestId getId();

    Date getRequestDate();

    String getRequestType();

    boolean isValid();

    ExpenseUser getUser();
}
