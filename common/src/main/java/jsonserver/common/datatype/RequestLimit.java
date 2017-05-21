package jsonserver.common.datatype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jsonserver.common.datatype.Validation.Validation;

/**
 * Created by olof on 2016-09-15.
 */
public class RequestLimit implements Validation
{
    private int lowerLimit;
    private int upperLimit;

    public RequestLimit()
    {
    }

    public RequestLimit(int lower, int upper)
    {
        lowerLimit = lower;
        upperLimit = upper;
    }

    public int getLowerLimit()
    {
        return lowerLimit;
    }


    public int getUpperLimit()

    {
        return upperLimit;
    }

    @Override
    public String toString()
    {
        return "   lowerLimit: " + lowerLimit + ", upperLimit: " + upperLimit + "\n";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RequestLimit that = (RequestLimit) o;

        if (lowerLimit != that.lowerLimit)
            return false;
        return upperLimit == that.upperLimit;

    }

    @Override
    public int hashCode()
    {
        int result = lowerLimit;
        result = 31 * result + upperLimit;
        return result;
    }

    @JsonIgnore
    @Override
    public boolean validate()
    {
        return lowerLimit < upperLimit;
    }
}
