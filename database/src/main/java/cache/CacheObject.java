package cache;

/**
 * Created by Foten on 4/25/2017.
 */
public class CacheObject<T>
{
    private long myLastAccessed = System.currentTimeMillis();
    private final T myValue;

    public CacheObject(T value)
    {
        this.myValue = value;
    }

    public T getValue()
    {
        return myValue;
    }
    public void updateLastAccessed(Long lastAccessed)
    {
        this.myLastAccessed = lastAccessed;
    }

    public Long getLastAccessed()
    {
        return myLastAccessed;
    }

    @Override
    public String toString()
    {
        return "LastAccessed: " + myLastAccessed + "Value" + myValue;
    }
}
