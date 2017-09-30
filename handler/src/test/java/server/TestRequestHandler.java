package server;

import jsonserver.common.containers.UserContainer;
import jsonserver.common.view.DbView;
import jsonserver.common.view.Expense.ExpensePutRequest;
import jsonserver.common.view.GetRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link RequestHandler}
 */
public class TestRequestHandler
{
    private static final String VALID_REQUEST_PUT = "{\"id\":{\"requestId\":\"Expenses\"},\"requestType\":\"Put\",\"requestDate\":\"2017-09-16T13:53:00.933Z\",\"user\":{\"userId\":\"remove_me2\",\"username\":\"remove_me2\",\"password\":\"remove_me2\"},\"content\":{\"cost\":\"200\",\"costType\":\"bill\",\"comment\":\"A comment\",\"buyDate\":\"2017-09-16T13:53:00.933Z\",\"uuid\":\"idToBeRemoved\"}}";
    private static final String VALID_REQUEST_GET = "{\"id\":{\"requestId\":\"Expenses\"},\"requestType\":\"Get\",\"requestDate\":\"2017-09-30\",\"order\":{\"orderBy\":\"buyDate\",\"isAscending\":true},\"limit\":{\"fetchperiod\":{\"period\":\"timePeriod\",\"periodToFetch\":\"All\",\"hasPeriod\":true,\"timeStart\":\"1970-01-01\",\"timeEnd\":\"9999-12-31\"}},\"user\":{\"userId\":\"remove_me4\",\"username\":\"remove_me4\",\"password\":\"remove_me4\"}}";

    private UserContainer userContainerForPut = mock(UserContainer.class);
    private UserContainer userContainerForGet = mock(UserContainer.class);
    private RequestHandler myRequestHandler;
    private DbView myDbView;

    @Rule
    public ExpectedException myExpectedException = ExpectedException.none();

    @Before
    public void init()
    {
        myDbView = mock(DbView.class);
        myRequestHandler = new RequestHandler(myDbView);
    }

    @Test
    public void testCreatePutRequest() throws Exception
    {
        when(myDbView.createUserContainer(any(ExpensePutRequest.class))).thenReturn(userContainerForPut);
        UserContainer userContainer = myRequestHandler.generateRequest(VALID_REQUEST_PUT);
        assertThat(userContainer).isEqualTo(userContainerForPut);

        verify(myDbView).createUserContainer(any(ExpensePutRequest.class));
    }

    @Test
    public void testCreateGetRequest() throws Exception
    {
        when(myDbView.createUserContainer(any(GetRequest.class))).thenReturn(userContainerForGet);
        UserContainer cachedContainer = myRequestHandler.generateRequest(VALID_REQUEST_GET);
        assertThat(cachedContainer).isEqualTo(userContainerForGet);
        verify(myDbView).createUserContainer(any(GetRequest.class));
    }

    @Test
    public void testCreateGetWithException() throws Exception
    {
        myExpectedException.expect(IllegalStateException.class);
        myExpectedException.expectMessage("Unable to create UserContainer");
        when(myDbView.createUserContainer(any(GetRequest.class))).thenThrow(new SQLException());

        UserContainer cachedContainer = myRequestHandler.generateRequest(VALID_REQUEST_GET);
        assertThat(cachedContainer).isNull();
    }
}
