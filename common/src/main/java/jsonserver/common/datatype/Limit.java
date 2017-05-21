package jsonserver.common.datatype;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jsonserver.common.datatype.Validation.Validation;

/**
 * Created by olof on 2016-09-15.
 */
public class Limit implements Validation
{
    private Fetchperiod fetchperiod;
    private RequestLimit requestLimit;

    public Limit()
    {

    }

    public Limit(Fetchperiod fetchperiod, RequestLimit requestLimit)
    {
        this.fetchperiod = fetchperiod;
        this.requestLimit = requestLimit;

    }

    public Fetchperiod getFetchperiod()
    {
        return fetchperiod;
    }

    public RequestLimit getRequestLimit()
    {
        return requestLimit;
    }

    @Override
    public String toString()
    {
        return "FetchPeriod: " + fetchperiod +
                "\n RequestLimit: " + requestLimit;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Limit limit = (Limit) o;

        if (fetchperiod != null ? !fetchperiod.equals(limit.fetchperiod) : limit.fetchperiod != null)
        {
            return false;
        }
        return requestLimit != null ? requestLimit.equals(limit.requestLimit) : limit.requestLimit == null;

    }

    @Override
    public int hashCode()
    {
        int result = fetchperiod != null ? fetchperiod.hashCode() : 0;
        result = 31 * result + (requestLimit != null ? requestLimit.hashCode() : 0);
        return result;
    }

    @JsonIgnore
    @Override
    public boolean validate()
    {
        return validate(fetchperiod) && validate(requestLimit);
    }

    private boolean validate(Validation validation)
    {
        return validation == null || validation.validate();
    }
}
