package request.impl;

import jsonserver.common.Utils.DateUtils;
import jsonserver.common.Utils.Utilities;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jsonserver.common.datatype.*;
import org.junit.Test;

import java.io.IOException;

import static jsonserver.common.Utils.Utilities.print;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by olof on 2016-07-11.
 */
public class TestGetDataRequest
{

    @Test
    public void testCreateGetDataRequest() throws  IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        Limit limit = new Limit(new Fetchperiod(false, null, null, null, null), new RequestLimit(0, 10));
        Order order = new Order("buyDate", true);

        ExpenseUser user = new ExpenseUser("userId", "name", "password");

        GetDataRequestImpl testJson = new GetDataRequestImpl(new RequestId("test"),
                "Get",
                order,
                DateUtils.getFirstDayOfMonthInSql(),
                limit,
                user);

        String json2 = mapper.writeValueAsString(testJson);

        print(json2);

        GetDataRequestImpl request = mapper.readValue(getJsonString(), GetDataRequestImpl.class);

        assertThat(request).isEqualTo(testJson);
    }
    private String getJsonString()
    {
        return
                "{" +
                        "\"id\": {" +
                        "\"requestId\": \"test\"" +
                        "}," +
                        " \"requestDate\": \"2016-09-11\"," +
                        "\"requestType\": \"Get\"," +
                        "\"order\":{" +
                        "\"orderBy\":\"buyDate\"," +
                        "\"isAscending\": true " +
                        "}," +
                        "\"limit\": {" +
                        "        \"fetchperiod\": {" +
                        "}," +
                        "\"requestLimit\": {" +
                        "    \"lowerLimit\": 0," +
                        "    \"upperLimit\": 10" +
                        "}" +
                        "}" +
                        "}";
    }

}
