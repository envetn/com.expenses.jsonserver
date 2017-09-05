package server.internal;

import com.google.gson.JsonObject;
import jsonserver.common.view.Request;
import request.requestcreator.JsonRequestFactory;


/**
 * Created by lofie on 2017-05-17.
 */
public class CachedRequest
{
    Request myRequest;
    JsonRequestFactory myRequestCreator;

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
