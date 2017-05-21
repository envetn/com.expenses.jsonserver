package jsonserver.common.view.Temperature;

import jsonserver.common.view.Request;

/**
 * Created by olof on 2016-07-09.
 */
public interface TemperaturePutRequest extends Request
{
    String getRequestTime();

    String getTemperature();
}

