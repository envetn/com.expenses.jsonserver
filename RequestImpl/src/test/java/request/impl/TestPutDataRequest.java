package request.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jsonserver.common.datatype.Content;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.RequestId;
import org.junit.Test;

import java.io.IOException;
import java.sql.Date;

import static jsonserver.common.Utils.DateUtils.getFirstDayOfMonthInSql;
import static jsonserver.common.Utils.Utilities.print;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by olof on 2016-09-17.
 */
public class TestPutDataRequest
{
    private static final Date DATE = getFirstDayOfMonthInSql();
    @Test
    public void testCreatePutRequest() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        Content content = new Content("65", "comment", "Mat", DATE, "34ae7e78329a4cabb63de0955f542f5a");
        ExpenseUser user = new ExpenseUser("userId", "name", "password");
        ExpensesPutRequestImpl putRequestWithJacksson = new ExpensesPutRequestImpl(new RequestId("test"),
                getFirstDayOfMonthInSql(),
                "Put",
                content,
                user);

        String json = mapper.writeValueAsString(putRequestWithJacksson);

        print(json);
        print("\n");

        ExpensesPutRequestImpl request = mapper.readValue(getJsonString(), ExpensesPutRequestImpl.class);
        assertThat(request).isEqualTo(putRequestWithJacksson);
    }



    private String getJsonString()
    {
        return "{" +
                " \"requestDate\": \""+DATE+"\"," +
                " \"requestType\": \"Put\"," +
                " \"id\": {" +
                "     \"requestId\": \"test\"" +
                " }," +
                "\"content\": {" +
                "     \"comment\": \"comment\"," +
                "     \"cost\": \"65\"," +
                "     \"uuid\": \"34ae7e78329a4cabb63de0955f542f5a\"," +
                "     \"costType\": \"Mat\"," +
                "     \"uuid\": \"34ae7e78329a4cabb63de0955f542f5a\"" +
                "}" +
                "}";
    }
}
