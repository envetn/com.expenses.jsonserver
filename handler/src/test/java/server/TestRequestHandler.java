package server;

import jsonserver.common.containers.UserContainer;
import org.junit.Ignore;
import org.junit.Test;
import server.internal.CachedRequest;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Created by lofie on 2017-09-16.
 */
public class TestRequestHandler
{
    private static final String VALID_REQUEST_GET_2020 = "{\"id\":{\"requestId\":\"Expenses\"},\"requestType\":\"Put\",\"requestDate\":\"2020-09-16T13:53:00.933Z\",\"user\":{\"userId\":\"remove_me2\",\"username\":\"remove_me2\",\"password\":\"remove_me2\"},\"content\":{\"cost\":\"200\",\"costType\":\"bill\",\"comment\":\"A comment\",\"buyDate\":\"2017-09-16T13:53:00.933Z\",\"uuid\":\"idToBeRemoved\"}}";
    private static final String VALID_REQUEST_GET = "{\"id\":{\"requestId\":\"Expenses\"},\"requestType\":\"Put\",\"requestDate\":\"2017-09-16T13:53:00.933Z\",\"user\":{\"userId\":\"remove_me2\",\"username\":\"remove_me2\",\"password\":\"remove_me2\"},\"content\":{\"cost\":\"200\",\"costType\":\"bill\",\"comment\":\"A comment\",\"buyDate\":\"2017-09-16T13:53:00.933Z\",\"uuid\":\"idToBeRemoved\"}}";

    @Test
    @Ignore
    public void testCreateTwiceWithIdentical() throws IOException
    {
        RequestHandler requestHandler = new RequestHandler(true);

        UserContainer cachedContainer = requestHandler.generateRequest(VALID_REQUEST_GET);

        UserContainer secondCachedContainer = requestHandler.generateRequest(VALID_REQUEST_GET);

        assertThat(cachedContainer).isSameAs(secondCachedContainer);
    }

    @Test
    @Ignore
    public void testTwoDifferentTimestampOnRequestDiffers() throws IOException
    {
        RequestHandler requestHandler = new RequestHandler(true);

        UserContainer cachedContainer= requestHandler.generateRequest(VALID_REQUEST_GET);

        UserContainer secondCachedContainer = requestHandler.generateRequest(VALID_REQUEST_GET_2020);

        assertThat(cachedContainer).isNotSameAs(secondCachedContainer);
    }
}
