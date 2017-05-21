package cache;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 *  Test Class for {@link MemoryCache}
 */
public class TestMemoryCache
{
    @Test
    public void testGetObject()
    {
        MemoryCache<String, String> cache = new MemoryCache<>(1, 1, 2);

        cache.put("key1", "value1");
        cache.put("key2", "value2");

        String val1 = cache.get("key1");
        assertThat(val1).isEqualTo("value1");

        String key3 = cache.get("key3");
        assertThat(key3).isNull();
    }

    @Test
    public void testRemoveObject()
    {
        MemoryCache<String, String> cache = new MemoryCache<>(200, 500, 6);

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        cache.put("key4", "value4");
        cache.put("key5", "value5");
        cache.put("key6", "value6");

        cache.remove("key2");
        assertThat(cache.size())
                .as("One object removed..")
                .isEqualTo(5);

        cache.put("Twitter", "Twitter");
        cache.put("SAP", "SAP");
        assertThat(cache.size())
                .as("Two objects Added but reached maxItems..")
                .isEqualTo(6);
    }

    @Test
    public void testCacheExpires() throws InterruptedException
    {
        MemoryCache<String, String> cache = new MemoryCache<>(1, 1, 6);

        cache.put("key1", "value1");
        cache.put("key2", "value2");

        // Adding 3 seconds sleep.. Both above objects will be removed from
        // Cache because of timeToLiveInSeconds myValue
        assertThat(cache.size()).isEqualTo(2);
        Thread.sleep(1040);

        System.out.println("Two objects are added but reached timeToLive. cache.size(): " + cache.size());
        assertThat(cache.size()).isEqualTo(0);
    }

    @Test
    public void testCleanupTime() throws InterruptedException
    {
        int size = 20000;

        //cleanup time for 2500000 objects are 0.608
        //cleanup time for 5000000 objects are 1.165
        //cleanup time for 10000000 objects are 4.955

        MemoryCache<String, String> cache = new MemoryCache<>(100, 100, size);

        for (int i = 0; i < size; i++)
        {
            String value = Integer.toString(i);
            cache.put(value, value);
        }
        System.out.println("Done adding values..");

        Thread.sleep(200);
        long start = System.currentTimeMillis();
        cache.cleanup();

        long l = System.currentTimeMillis() - start;
        double finish = l / 1000.0;

        System.out.println("cleanup time for " + size + " objects are " + finish);
        assertThat(start).isGreaterThan(l);

    }

}
