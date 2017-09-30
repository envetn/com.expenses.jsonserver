package jsonserver.common.datatype.Validation;

/**
 * Created by Foten on 12/10/2016.
 */

public class ValidationUtil
{
    public boolean validate(Validation validate, boolean canBeNull)
    {
        boolean isValid = true;
        if(canBeNull) // allowed to be null
        {
            if(isNull(validate)) // if not null, validate
            {
                isValid = validate.validate();
            }
        }
        else  //not allowed to be null
        {
            // check not null
            isValid = isNull(validate) && validate.validate();
        }
        return isValid;
    }

    public static boolean validDate(String date)
    {
        return date.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]");
    }

    public static boolean isNull(Object ...validate)
    {
        for (Object object : validate)
        {
            if(object == null)
            {
                return false;
            }
        }
        return true;
    }
}
