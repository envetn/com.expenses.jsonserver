package jsonserver.common.datatype;

import jsonserver.common.view.Request;

import java.util.List;

/**
 * Created by lofie on 2017-05-21.
 */
public class UserContainer
{
    private final Request myRequest;

    private final List<Expenses> myExpenses;
    private final List<Temperature> myTemperature;
    private final List<Threshold> myThesholds;

    public UserContainer(Request request, List<Expenses> expenses, List<Threshold> thresholds, List<Temperature> temperature)
    {
        myRequest = request;
        myExpenses = expenses;
        myThesholds = thresholds;
        myTemperature = temperature;
    }


    public Request getRequest()
    {
        return myRequest;
    }


    public List<Threshold> getThresholds()
    {
        return myThesholds;
    }

    public List<Expenses> getExpenses()
    {
        return myExpenses;
    }
}
