package jsonserver.common.view;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.UserContainer;

import java.sql.SQLException;

/**
 * Created by Foten on 4/9/2017.
 */
public interface DbView
{
    UserContainer createUserContainer(Request request) throws SQLException;

    JsonObject readFromContainer(UserContainer container);

    JsonObject putIntoContainer(UserContainer container);

    boolean saveDatabaseChanges(String type, UserContainer container) throws SQLException;

    JsonObject removeFromContainer(UserContainer container);
}
