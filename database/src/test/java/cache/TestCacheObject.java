package cache;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 *  Test Class for {@link CacheObject}
 */
public class TestCacheObject
{
    @Test
    public void testCreateCacheObject()
    {
        CacheObject<String> cacheObject = new CacheObject<>("Nisse");
        assertThat(cacheObject.getValue()).isEqualTo("Nisse");
        assertThat(cacheObject.getLastAccessed()).isLessThan(System.currentTimeMillis());
    }

    @Test
    public void testUpdateLastAccessed()
    {
        CacheObject<String> cacheObject = new CacheObject<>("Nisse");

        Long newLastAccessed = System.currentTimeMillis();
        cacheObject.updateLastAccessed(newLastAccessed);

        assertThat(cacheObject.getLastAccessed()).isEqualTo(newLastAccessed);
    }
}
