package Connection;

import com.google.gson.JsonObject;
import jsonserver.common.datatype.*;
import jsonserver.common.view.GetRequest;
import jsonserver.common.view.Request;
import org.junit.Test;

import java.sql.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by lofie on 2017-05-23.
 */
public class TestExpensesDbConnection
{

    @Test
    public void test()
    {
        Date timeStart = new Date(System.currentTimeMillis());
        Date timeEnd = new Date(System.currentTimeMillis() + 100000);

        Fetchperiod fethperiodMock = new Fetchperiod(true, null, null, timeStart.toString(), timeEnd.toString())

        Limit limitMock = mock(Limit.class);
        when(limitMock.getFetchperiod()).thenReturn(fethperiodMock);

        GetRequest getRequest = mock(GetRequest.class);
        when(getRequest.getLimit()).thenReturn(limitMock);

        

    }
}
