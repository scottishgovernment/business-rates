package scot.mygov.business.rates.config;

import java.net.Proxy;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;

public class ProxyConfigurationTest {

    @Test
    public void withProxyTest() {
        ProxyConfiguration configuration = new ProxyConfiguration();
        ReflectionTestUtils.setField(configuration, "saaProxyHost", "192.168.41.8");

        Assert.assertTrue(configuration.saaRestTemplate().getRequestFactory() instanceof SimpleClientHttpRequestFactory);

        SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory) configuration.saaRestTemplate().getRequestFactory();
        Proxy proxy = (Proxy) ReflectionTestUtils.getField(requestFactory, "proxy");

        Assert.assertNotNull(proxy);
        Assert.assertTrue(proxy.toString().contains("192.168.41.8"));

    }

    @Test
    public void withoutProxyTest() {
        ProxyConfiguration configuration = new ProxyConfiguration();
        ReflectionTestUtils.setField(configuration, "saaProxyHost", "");

        ClientHttpRequestFactory requestFactory = configuration.saaRestTemplate().getRequestFactory();

        Assert.assertNull(ReflectionTestUtils.getField(requestFactory, "proxy"));

    }
}
