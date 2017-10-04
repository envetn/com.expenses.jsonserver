package Connection.internal;

import jsonserver.common.containers.UserContainer;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.view.Request;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lofie on 2017-09-30.
 */
public class UserConnection
{
    private static final Logger LOGGER = Logger.getLogger(UserConnection.class);
    private final Connection myConnection;

    public UserConnection(Connection connection)
    {
        myConnection = connection;
    }

    public boolean doesUserExist(ExpenseUser user) throws SQLException
    {
        return getUser(user).next();
    }

    public int createUser(UserContainer container, Request createUserRequest) throws SQLException
    {
        if (container.isUserUpdated())
        {
            ExpenseUser user = createUserRequest.getUser();

            String sql = "INSERT INTO %s (username, passwd, created) VALUES (?,?,?)";
            PreparedStatement preparedStatement = myConnection.prepareStatement(String.format(sql, "test.expenseuser"));
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setDate(3, createUserRequest.getRequestDate());

            LOGGER.info("Statement: " + preparedStatement.toString());
            return preparedStatement.executeUpdate();
        }
        LOGGER.error("User has not been changed, no need to update");
        return 0;
    }

    public int removeUserData(UserContainer container, Request request)
    {
        // remove every expenses connected to the user.

        // Remove every temperature connected to the user

        // Last, remove the user itself
        LOGGER.error("Removing user data not supported in this release");

        return 0;
    }

    private ResultSet getUser(ExpenseUser user) throws SQLException
    {
        String sqlString = "SELECT * FROM test.expenseuser where username = ? AND passwd = ?";
        PreparedStatement preparedStatement = myConnection.prepareStatement(sqlString);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());

        LOGGER.info(sqlString);
        return preparedStatement.executeQuery();
    }
}
