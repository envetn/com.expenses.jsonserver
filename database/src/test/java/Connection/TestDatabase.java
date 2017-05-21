package Connection;

import com.google.gson.JsonObject;
import jsonserver.common.Utils.Utilities;
import jsonserver.common.datatype.Content;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.RequestId;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import jsonserver.common.view.DbView;
import jsonserver.common.view.DeleteRequest;
import jsonserver.common.view.Expense.ExpensePutRequest;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by olof on 2016-10-26.
 */
public class TestDatabase
{
    private DbView myConnection;
    private RequestId EXPENSES_REQUEST_ID = new RequestId("Expenses");
    private RequestId USER_REQUEST_ID = new RequestId("User");

    @Test
    public void testUpdateThreshold() throws ClassNotFoundException, SQLException
    {
        Content contentMock = mock(Content.class);
        when(contentMock.getbuyDate()).thenReturn(new Date(System.currentTimeMillis()));
        when(contentMock.getCostType()).thenReturn("food");
        when(contentMock.getCost()).thenReturn("200");

        ExpenseUser userMock = mock(ExpenseUser.class);
        when(userMock.getUsername()).thenReturn("lofie");

        ExpensePutRequest putRequest = mock(ExpensePutRequest.class);

        when(putRequest.getContent()).thenReturn(contentMock);
        when(putRequest.getUser()).thenReturn(userMock);


        DatabaseConnection connection = new DatabaseConnection();
        connection.openConnection();

        int jsonObject = connection.updateThresholdValues(putRequest);
        connection.closeConnection();
        System.out.println(jsonObject);



    }

    @Before
    public void init() throws ClassNotFoundException
    {
//        ExpenseUser user = mockExpenseUser();
//        CreateUserRequest userRequest = mock(CreateUserRequest.class);
//        when(userRequest.getUser()).thenReturn(user);
//        when(userRequest.getRequestDate()).thenReturn(Utilities.getFirstDayOfMonthInSql());
//        when(userRequest.getId()).thenReturn(USER_REQUEST_ID);
//
//        Content contentMock = mock(Content.class);
//        when(contentMock.getComment()).thenReturn("remove_me");
//        when(contentMock.getCost()).thenReturn("remove_me");
//        when(contentMock.getCostType()).thenReturn("remove_me");
//        when(contentMock.getUUID()).thenReturn("remove_me");
//
//        ExpensePutRequest putMock = mock(ExpensePutRequest.class);
//        when(putMock.getUser()).thenReturn(user);
//        when(putMock.getId()).thenReturn(new RequestId("Expenses"));
//        when(putMock.getRequestDate()).thenReturn(Utilities.getLastDayOfMonth());
//        when(putMock.getContent()).thenReturn(contentMock);

        myConnection = DatabaseConnection.DatabaseBuilder.initDatabase();
        myConnection.openConnection();
//        myConnection.executePutUser(userRequest);
//        myConnection.insertExpensesIntoDatabase(putMock);
    }

    @After
    public void after()
    {
        myConnection.closeConnection();
    }

    @Ignore
    @Test
    public void testCreateDeleteSql() throws ClassNotFoundException
    {
//        DeleteRequest deleteRequestMock = mock(DeleteRequest.class);
//        when(deleteRequestMock.getIdToRemove()).thenReturn(Arrays.asList("id1", "id2"));
//        when(deleteRequestMock.getId()).thenReturn(EXPENSES_REQUEST_ID);
//
//        Integer sqlResult = myConnection.removeFromDatabase(deleteRequestMock);
//        JsonObject jsonResponse = JsonResponseHandler.createDefaultResponse(sqlResult, EXPENSES_REQUEST_ID, "Remove");
//        String responseCode = jsonResponse.get("Response").getAsString();
//
//        assertThat(responseCode).isEqualTo("Success");
    }

    @Test
    @Ignore
    public void testFailedRemoveEntry() throws ClassNotFoundException, SQLException
    {
        ExpenseUser user = mockExpenseUser();

        Content contentMock = mock(Content.class);
        when(contentMock.getComment()).thenReturn("remove_me");
        when(contentMock.getCost()).thenReturn("remove_me");
        when(contentMock.getCostType()).thenReturn("remove_me");
        when(contentMock.getUUID()).thenReturn("remove_me");

        ExpensePutRequest putMock = mock(ExpensePutRequest.class);
        when(putMock.getUser()).thenReturn(user);
        when(putMock.getId()).thenReturn(new RequestId("Expenses"));
        when(putMock.getRequestDate()).thenReturn(Utilities.getLastDayOfMonth());
        when(putMock.getContent()).thenReturn(contentMock);

        DbView connection = DatabaseConnection.DatabaseBuilder.initDatabase();
//        connection.insertExpensesIntoDatabase(putMock);

        DeleteRequest deleteMock = mock(DeleteRequest.class);
//        when(deleteMock.getUser()).thenReturn(user);
        when(deleteMock.getId()).thenReturn(new RequestId("Expenses"));
        when(deleteMock.getRequestDate()).thenReturn(Utilities.getLastDayOfMonth());
        when(deleteMock.getIdToRemove()).thenReturn(Arrays.asList("removeMe1", "remove_me"));

//        Integer sqlResult = myConnection.removeFromDatabase(deleteMock);
//        JsonObject jsonResponse = JsonResponseHandler.createDefaultResponse(sqlResult, EXPENSES_REQUEST_ID, "Remove");

    }


    private ExpenseUser mockExpenseUser()
    {
        ExpenseUser userMock = mock(ExpenseUser.class);
        when(userMock.getUserId()).thenReturn("remove_me");
        when(userMock.getUsername()).thenReturn("remove_me");
        when(userMock.getPassword()).thenReturn("remove_me");
        return userMock;
    }
}
