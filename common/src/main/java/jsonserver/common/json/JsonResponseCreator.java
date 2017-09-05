package jsonserver.common.json;

import com.google.gson.JsonObject;

/**
 * Created by lofie on 2017-07-16.
 */
public interface JsonResponseCreator
{
    JsonObject createPutResponse();

    JsonObject createGetResponse();
}
