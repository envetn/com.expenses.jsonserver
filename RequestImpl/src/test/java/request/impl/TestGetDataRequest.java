package request.impl;

import jsonserver.common.Utils.DateUtils;
import jsonserver.common.Utils.Utilities;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jsonserver.common.datatype.*;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.sql.Date;

import static jsonserver.common.Utils.Utilities.print;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by olof on 2016-07-11.
 */
public class TestGetDataRequest
{

    @Test
    @Ignore
    public void testCreateGetDataRequest() throws  IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        Limit limit = new Limit(new Fetchperiod(true, "All", "timePeriod", "1970-01-01", "9999-12-31"), null);
        Order order = new Order("buyDate", true);

        ExpenseUser user = new ExpenseUser("userId", "name", "password");

        GetDataRequestImpl testJson = new GetDataRequestImpl(new RequestId("test"),
                "Get",
                order,
                Date.valueOf("2016-10-01"),
                limit,
                user);

        String json2 = mapper.writeValueAsString(testJson);

        print("Json: " + json2);

        GetDataRequestImpl request = mapper.readValue(getJsonString(), GetDataRequestImpl.class);


        print("Expected: " + request);
        print("actual: " + testJson);
        assertThat(request).isEqualTo(testJson);
    }

    private String getJsonString()
    {
        return "{\n" +
                "\t\"id\": {\n" +
                "\t\t\"requestId\": \"test\"\n" +
                "\t},\n" +
                "\t\"requestType\": \"Get\",\n" +
                "\t\"requestDate\": \"2016-10-01\",\n" +
                "\t\"order\": {\n" +
                "\t\t\"orderBy\": \"buyDate\",\n" +
                "\t\t\"isAscending\": true\n" +
                "\t},\n" +
                "\t\"limit\": {\n" +
                "\t\t\"fetchperiod\": {\n" +
                "\t\t\t\"period\": \"timePeriod\",\n" +
                "\t\t\t\"periodToFetch\": \"All\",\n" +
                "\t\t\t\"hasPeriod\": true,\n" +
                "\t\t\t\"timeStart\": \"1970-01-01\",\n" +
                "\t\t\t\"timeEnd\": \"9999-12-31\"\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"user\": {\n" +
                "\t\t\"userId\": \"userId\",\n" +
                "\t\t\"username\": \"name\",\n" +
                "\t\t\"password\": \"password\"\n" +
                "\t}\n" +
                "}";
       /* return
                "{" +
                        "\"id\": {" +
                        "\"requestId\": \"test\"" +
                        "}," +
                        " \"requestDate\": \"2017-10-01\"," +
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
                        "}," +
                        "\"user\":{" +
                        "       \"userId\": userId" +
                        "       \"username\": name" +
                        "       \"password\": password" +
                        "}"+
                        "}";*/
    }

}
