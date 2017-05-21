package jsonserver.common.datatype;

import com.fasterxml.jackson.annotation.JsonProperty;
import jsonserver.common.datatype.Validation.Validation;

/**
 * Created by olof on 2016-10-03.
 */
public class ExpenseUser implements Validation
{
    @JsonProperty(value = "userId")
    private String userId;
    @JsonProperty(value = "username")
    private String username;
    @JsonProperty(value = "password")
    private String password;

    //
    public ExpenseUser()
    {
        //all null;
        //should not be possible?
    }

    //todo id should be int
    public ExpenseUser(String userId, String username, String password)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
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

        ExpenseUser that = (ExpenseUser) o;

        return username.equals(that.username) && password.equals(that.password);
    }

    @Override
    public int hashCode()
    {
        int result = userId.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }

    @Override
    public boolean validate()
    {
        return notNullOrEmpty(userId) && notNullOrEmpty(username) && notNullOrEmpty(password);
    }

    private boolean notNullOrEmpty(String value)
    {
        return value != null && !value.isEmpty();

    }

    @Override
    public String toString()
    {
        return "[ userId=" + userId + ", username=" + username + ", password=" + password +"]";
    }
}
