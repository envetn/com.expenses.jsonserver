package Connection.internal;

import jsonserver.common.containers.UserContainer;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.view.Request;
import org.junit.Test;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by lofie on 2017-10-15.
 */
public class TestUserConnection
{

    public static final ExpenseUser EXPENSE_USER = new ExpenseUser("Nisse", "Nisse", "Nisse");
    public static final int SQL_SUCCESS = 1;

    @Test
    public void testUserConnection() throws SQLException
    {
        Request requestMock = mock(Request.class);
        when(requestMock.getUser()).thenReturn(EXPENSE_USER);

        ResultSet resultSetMock = mock(ResultSet.class);
        when(resultSetMock.next()).thenReturn(true);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        Connection connectionMock = mock(Connection.class);
        when(connectionMock.prepareStatement("SELECT * FROM test.expenseuser where username = ? AND passwd = ?")).thenReturn(preparedStatementMock);

        UserConnection userConnection = new UserConnection(connectionMock);

        boolean userExist = userConnection.doesUserExist(requestMock);
        assertThat(userExist).isTrue();
    }


    @Test
    public void testCreateUser() throws SQLException
    {
        Date date = Date.valueOf("2017-10-03");

        Request requestMock = mock(Request.class);
        when(requestMock.getUser()).thenReturn(EXPENSE_USER);
        when(requestMock.getRequestDate()).thenReturn(date);

        ResultSet resultSetMock = mock(ResultSet.class);
        when(resultSetMock.next()).thenReturn(true);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(preparedStatementMock.executeUpdate()).thenReturn(SQL_SUCCESS);

        UserContainer userContainer = mock(UserContainer.class);
        when(userContainer.isUserUpdated()).thenReturn(true);

        Connection connectionMock = mock(Connection.class);
        when(connectionMock.prepareStatement("INSERT INTO test.expenseuser (username, passwd, created) VALUES (?,?,?)")).thenReturn(preparedStatementMock);
        UserConnection userConnection = new UserConnection(connectionMock);

        int sqlSuccess = userConnection.createUser(userContainer, requestMock);
        assertThat(sqlSuccess).isEqualTo(SQL_SUCCESS);

        verify(preparedStatementMock).setString(1, EXPENSE_USER.getUsername());
        verify(preparedStatementMock).setString(2, EXPENSE_USER.getPassword());
        verify(preparedStatementMock).setDate(3, date);
    }

}