package server;

import org.junit.Test;
import server.internal.CachedRequest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Created by lofie on 2017-09-16.
 */
public class TestRequestHandler
{
    private static final String VALID_REQUEST_GET_2020 = "{\"id\":{\"requestId\":\"Expenses\"},\"requestType\":\"Put\",\"requestDate\":\"2020-09-16T13:53:00.933Z\",\"user\":{\"userId\":\"remove_me2\",\"username\":\"remove_me2\",\"password\":\"remove_me2\"},\"content\":{\"cost\":\"200\",\"costType\":\"bill\",\"comment\":\"A comment\",\"buyDate\":\"2017-09-16T13:53:00.933Z\",\"uuid\":\"idToBeRemoved\"}}";
    private static final String VALID_REQUEST_GET = "{\"id\":{\"requestId\":\"Expenses\"},\"requestType\":\"Put\",\"requestDate\":\"2017-09-16T13:53:00.933Z\",\"user\":{\"userId\":\"remove_me2\",\"username\":\"remove_me2\",\"password\":\"remove_me2\"},\"content\":{\"cost\":\"200\",\"costType\":\"bill\",\"comment\":\"A comment\",\"buyDate\":\"2017-09-16T13:53:00.933Z\",\"uuid\":\"idToBeRemoved\"}}";

    @Test
    public void testCreateTwiceWithIdentical()
    {
        RequestHandler requestHandler = new RequestHandler();

        CachedRequest cachedRequest = requestHandler.generateRequest(VALID_REQUEST_GET);

        CachedRequest secondCachedRequest = requestHandler.generateRequest(VALID_REQUEST_GET);

        assertThat(cachedRequest).isSameAs(secondCachedRequest);
    }
    @Test
    public void testTwoDifferentTimestampOnRequestDiffers()
    {
        RequestHandler requestHandler = new RequestHandler();

        CachedRequest cachedRequest = requestHandler.generateRequest(VALID_REQUEST_GET);

        CachedRequest secondCachedRequest = requestHandler.generateRequest(VALID_REQUEST_GET_2020);

        assertThat(cachedRequest).isNotSameAs(secondCachedRequest);
    }
}
