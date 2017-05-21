package request.impl;


import jsonserver.common.Utils.Utilities;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.RequestId;
import org.junit.Test;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static jsonserver.common.Utils.Utilities.print;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by olof on 2016-10-28.
 */
public class TestDeleteRequest
{
    private static final Date DATE =  Utilities.getFirstDayOfMonthInSql();
    @Test
    public void testDeleteRequest() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        List<String> idsToRemove = new ArrayList<>();
        idsToRemove.add("RemoveId");
        idsToRemove.add("RemoveId20");
        ExpenseUser user = new ExpenseUser("userId", "name", "password");

        DeleteRequestImpl testJson = new DeleteRequestImpl(new RequestId("test"),
                DATE,
                "Delete",
                idsToRemove,
                user);

        String json2 = mapper.writeValueAsString(testJson);

        print(json2);

        DeleteRequestImpl request = mapper.readValue(getJsonString(), DeleteRequestImpl.class);

        assertThat(request).isEqualTo(testJson);
    }

    private String getJsonString()
    {
        return
                "{" +
                        "\"id\": {" +
                        "\"requestId\": \"test\"" +
                        "}," +
                        " \"requestDate\": \""+DATE+"\"," +
                        "\"requestType\": \"Delete\"," +
                        "\"remove-Data\": [\"RemoveId\", \"RemoveId20\"],"+
                        "\"user\":{ "+
                        "\"userId\":\"userId\","+
                        "\"username\":\"name\","+
                        "\"password\":\"password\""+
                        "}" +
                        "}";
    }
}
