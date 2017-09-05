package request.impl;

import jsonserver.common.Utils.DateUtils;
import jsonserver.common.Utils.Utilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.RequestId;
import org.junit.Ignore;
import org.junit.Test;
import jsonserver.common.view.Temperature.TemperaturePutRequest;

import java.io.IOException;
import java.sql.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by Foten on 2/5/2017.
 */
@Ignore
public class TestCreateTemperaturePutRequest
{
    @Test
    public void testCreateTemperature() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        Date firstDayOfMonthInSql = DateUtils.getFirstDayOfMonthInSql();
        RequestId requestId = new RequestId("Temperature");
        String temperature = "25.2";
        String time = "20:24:20";
        ExpenseUser user = new ExpenseUser("UserId", "UserName", "Password");

        TemperaturePutRequest temperaturePutRequest = new TemperaturePutRequestImpl(requestId, time, firstDayOfMonthInSql, temperature, "Put", user);
        String json2 = mapper.writeValueAsString(temperaturePutRequest);

        String jsonString = getJsonString();
        TemperaturePutRequest temperatureRequestWithJacksson = mapper.readValue(jsonString, TemperaturePutRequestImpl.class);

        assertThat(temperatureRequestWithJacksson).isEqualTo(temperaturePutRequest);
        assertThat(json2)
                .isNotNull()
                .contains(temperature)
                .contains(time)
                .contains("Temperature")
                .contains(firstDayOfMonthInSql.toString());
    }

    private String getJsonString()
    {
        Date firstDayOfMonthInSql = DateUtils.getFirstDayOfMonthInSql();
        return "{" +
                " \"id\": {" +
                "\"requestId\": \"Temperature\"" +
                " }," +
                "\"user\": {" +
                "     \"userId\": \"comment\"," +
                "     \"username\": \"65\"," +
                "     \"password\": \"34ae7e78329a4cabb63de0955f542f5a\"" +
                "}," +
                " \"requestDate\": \""+firstDayOfMonthInSql.toString()+"\"," +
                " \"requestType\": \"Put\"," +
                "\"temperature\": \"25.2\"," +
                "\"time\": \"20:24:20\"" +
                "}";
    }
}
