package jsonserver.common.exception;

/**
 * Created by lofie on 2017-07-24.
 */
public class InvalidRequestException extends RuntimeException
{
    public InvalidRequestException(String message)
    {
        super(message);
    }
}
