package server.internal;

import com.google.gson.JsonObject;
import jsonserver.common.view.Request;
import request.requestcreator.JsonRequestCreator;


/**
 * Created by lofie on 2017-05-17.
 */
public class CachedRequest
{
    Request myRequest;
    JsonRequestCreator myRequestCreator;

    public CachedRequest(Request request, JsonRequestCreator requestCreator)
    {
        myRequest = request;
        myRequestCreator = requestCreator;
    }

    public JsonObject execute()
    {
        return myRequestCreator.executeRequest(myRequest);
    }

    public Request getRequest()
    {
        return myRequest;
    }

    public JsonRequestCreator getRequestCreator()
    {
        return myRequestCreator;
    }

    @Override
    public String toString()
    {
        return "CachedRequest{" +
                "myRequest=" + myRequest +
                "\n myRequestCreator=" + myRequestCreator +
                '}';
    }
}
