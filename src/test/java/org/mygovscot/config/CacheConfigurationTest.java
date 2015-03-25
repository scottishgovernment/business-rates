package org.mygovscot.config;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;

public class CacheConfigurationTest {

    @Test
    public void testEhCacheManagerFactoryBean() throws Exception {
        CacheConfiguration cc = new CacheConfiguration();
        EhCacheManagerFactoryBean bean = cc.ehCacheManagerFactoryBean();
        Assert.assertNotNull(bean);
        EhCacheCacheManager cm = cc.cacheManager(bean);
        Assert.assertNotNull(cm);
    }

}