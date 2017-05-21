package request.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.RequestId;
import org.junit.Test;

import java.io.IOException;

import static jsonserver.common.Utils.Utilities.getFirstDayOfMonthInSql;
import static jsonserver.common.Utils.Utilities.print;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Foten on 11/30/2016.
 */
public class TestCreateUserRequest
{
    @Test
    public void testCreateUserRequest() throws IOException
    {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        ExpenseUser user = new ExpenseUser("userId", "name", "password");
        CreateUserRequest putRequestWithJacksson = new CreateUserRequest(new RequestId("User"),
                getFirstDayOfMonthInSql(),
                "Put",
                user);

        String json2 = mapper.writeValueAsString(putRequestWithJacksson);

        print(json2);

        CreateUserRequest request = mapper.readValue(getJsonString(), CreateUserRequest.class);

        assertThat(request).isEqualTo(putRequestWithJacksson);
    }

    @Test
    public void testCreateInvalidUser() throws IOException
    {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

//        ExpenseUser user = new ExpenseUser("userId", "name", "password");
//        CreateUserRequest putRequestWithJacksson = new CreateUserRequest(new RequestId("User"),
//                getFirstDayOfMonthInSql(),
//                "Put",
//                user);
//
//        String json2 = mapper.writeValueAsString(putRequestWithJacksson);
//
//        print(json2);

        String invalidJson = getInvalidJson();
        CreateUserRequest request = mapper.readValue(invalidJson, CreateUserRequest.class);

        if(request != null)
        {

        }
//        assertThat(request).isEqualTo(putRequestWithJacksson);
    }

    private String getJsonString()
    {
        return "{" +
                " \"requestDate\": \"2016-09-01\"," +
                " \"requestType\": \"Put\"," +
                " \"id\": {" +
                "     \"requestId\": \"User\"" +
                " }," +
                "\"user\": {" +
                "     \"userId\": \"comment\"," +
                "     \"username\": \"65\"," +
                "     \"password\": \"34ae7e78329a4cabb63de0955f542f5a\"" +
                "}" +
                "}";
    }

    private String getInvalidJson()
    {
        return "{" +
                " \"requestDate\": \"2016-09-01\"," +
                " \"requestType\": \"Put\"," +
                " \"id\": {" +
                "     \"requestId\": \"User\"" +
                " }" +
                "}";
    }

}

