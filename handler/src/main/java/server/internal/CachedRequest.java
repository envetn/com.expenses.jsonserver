package server.internal;

import jsonserver.common.view.Request;
import request.requestcreator.JsonRequestFactory;


/**
 * Created by lofie on 2017-05-17.
 */
public class CachedRequest
{
    private final Request myRequest;
    private final JsonRequestFactory myRequestCreator;

    public CachedRequest(Request request, JsonRequestFactory requestCreator)
    {
        myRequest = request;
        myRequestCreator = requestCreator;
    }

    public Request getRequest()
    {
        return myRequest;
    }


}
