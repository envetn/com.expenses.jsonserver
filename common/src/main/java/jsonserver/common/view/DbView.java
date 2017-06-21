package jsonserver.common.view;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.UserContainer;
import jsonserver.common.view.Expense.ExpensePutRequest;
import jsonserver.common.view.Temperature.TemperaturePutRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Foten on 4/9/2017.
 */
public interface DbView
{
    void openConnection();

    void closeConnection();

    UserContainer createUserContainer(Request request) throws SQLException;

    ImmutableList<ExpenseUser> getCachedUsers();

    ImmutableList<ExpenseUser> loadUsers() throws SQLException;

    JsonObject readFromContainer(UserContainer container);
}
