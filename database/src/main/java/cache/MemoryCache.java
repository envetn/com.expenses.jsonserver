package cache;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by Foten on 4/25/2017.
 */
public class MemoryCache<K, T>
{
    //http://crunchify.com/how-to-create-a-simple-in-memory-cache-in-java-lightweight-cache/
    private static final Logger LOG = Logger.getLogger(MemoryCache.class);
    private static final Long DEFAULT_TTL = 200L;
    private static final Long DEFAULT_INTERVAL = 200L;
    private static final Integer DEFAULT_MAX_VALUES = 200;

    private long timeToLive;
    private boolean isClearning;
    private final LRUMap cacheMaop;


    /**
     * Constructor for Using default values
     */
    public MemoryCache()
    {
        this(DEFAULT_TTL, DEFAULT_INTERVAL, DEFAULT_MAX_VALUES);
    }

    public MemoryCache(long timeToLive, final long timeInterval, int maxItem)
    {
        this.timeToLive = timeToLive;
        this.cacheMaop = new LRUMap(maxItem);

        if (timeToLive > 0 && timeInterval > 0)
        {
            Thread thread = new Thread(() ->
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(timeInterval * 1000);
                    }
                    catch (InterruptedException e)
                    {
                        LOG.error("Error running memory cache: " + e);
                        break;
                    }
                    cleanup();
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    @SuppressWarnings("unchecked")
    public T get(K key)
    {
        T returnValue = null;
        if (!isClearning)
        {
            synchronized (cacheMaop)
            {
                CacheObject object = (CacheObject) cacheMaop.get(key);

                if (object != null)
                {
                    object.updateLastAccessed(System.currentTimeMillis());
                    returnValue = (T) object.getValue();
                }
            }
        }
        return returnValue;
    }

    public void put(K key, T value)
    {
        if (!isClearning)
        {
            synchronized (cacheMaop)
            {
                CacheObject<T> cacheObject = new CacheObject<>(value);
                cacheMaop.put(key, cacheObject);
            }
        }
    }

    public boolean remove(K key)
    {
        if (!isClearning)
        {
            synchronized (cacheMaop)
            {
                return cacheMaop.remove(key) != null;
            }
        }
        return false;
    }

    public int size()
    {
        if (!isClearning)
        {
            synchronized (cacheMaop)
            {
                return cacheMaop.size();
            }
        }
        return 0;
    }


    @SuppressWarnings("unchecked")
    public void cleanup()
    {
        LOG.info("Running cleanup...");
        isClearning = true;
        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey;

        synchronized (cacheMaop)
        {
            MapIterator itr = cacheMaop.mapIterator();
            deleteKey = new ArrayList<>((cacheMaop.size() / 2) + +1); //why?
            K key;
            CacheObject cacheObject;

            while (itr.hasNext())
            {
                key = (K) itr.next();
                cacheObject = (CacheObject) itr.getValue();

                if (cacheObject != null && (now > (timeToLive + cacheObject.getLastAccessed())))
                {
                    deleteKey.add(key);
                }
            }
        }

        for (K key : deleteKey)
        {
            synchronized (cacheMaop)
            {
                cacheMaop.remove(key);
            }
            Thread.yield();
        }
        isClearning = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        MapIterator itr = cacheMaop.mapIterator();

        while (itr.hasNext())
        {
            K key = (K) itr.next();
            CacheObject cacheObject = (CacheObject) itr.getValue();

            builder.append("Key: ")
                    .append(key)
                    .append("\n")
                    .append("Object: ")
                    .append(cacheObject);
        }

        return builder.toString();
    }
}

