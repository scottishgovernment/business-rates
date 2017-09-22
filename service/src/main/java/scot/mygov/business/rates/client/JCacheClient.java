package scot.mygov.business.rates.client;

import org.xnio.channels.UnsupportedOptionException;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.integration.CacheLoader;
import javax.cache.spi.CachingProvider;
import java.util.Map;

public class JCacheClient implements SAAClient {

    private final Cache<String, SAAResponse> cache;

    public JCacheClient(Cache cache) {
        this.cache = cache;
    }

    public static JCacheClient create(
            CachingProvider cachingProvider,
            SAAClient client,
            String name,
            Duration expiry) {
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CacheLoader<String, SAAResponse> loader = loader(client);
        MutableConfiguration<String, SAAResponse> config = new MutableConfiguration<String, SAAResponse>()
                .setTypes(String.class, SAAResponse.class)
                .setCacheLoaderFactory(() -> loader)
                .setStoreByValue(false)
                .setReadThrough(true)
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(expiry))
                .setStatisticsEnabled(true);
        Cache<String, SAAResponse> cache = cacheManager.createCache(name, config);
        return new JCacheClient(cache);
    }

    private static CacheLoader<String, SAAResponse> loader(final SAAClient client) {
        return new CacheLoader<String, SAAResponse>() {
                @Override
                public SAAResponse load(String key) {
                    return client.search(key);
                }

                @Override
                public Map<String, SAAResponse> loadAll(Iterable<? extends String> keys) {
                    throw new UnsupportedOptionException();
                }
            };
    }

    @Override
    public SAAResponse search(String address) {
        return cache.get(address);
    }

}
