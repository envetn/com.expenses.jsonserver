package jsonserver.common.datatype;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Created by Foten on 12/10/2016.
 */
public class TestFetchPeriod
{
    @Test
    public void testCreateMonthPeriod()
    {
        String startDate = "2016-12-01";
        String endDate = "2016-12-31";

        Fetchperiod fetchperiod = new Fetchperiod(true, "12", "timePeriod", startDate, endDate);
        boolean isValid = fetchperiod.validate();

        assertThat(isValid).isTrue();
    }

    @Test
    public void testCreateAllPeriod()
    {
        Fetchperiod fetchperiod = new Fetchperiod(true, "", "All", "", "");
        boolean isValid = fetchperiod.validate();

        assertThat(isValid).isTrue();
    }

    @Test
    public void testCreateDayPeriod()
    {
        Fetchperiod fetchperiod = new Fetchperiod(true, "2016-05-16", "day", "", "");
        boolean isValid = fetchperiod.validate();

        assertThat(isValid).isTrue();
    }
}
