package org.mygovscot.services;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mygovscot.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext
public class ProxyConfigurationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyConfigurationTest.class);

    @Autowired
    RestTemplate template;

    @BeforeClass
    public static void setSystemProps() {
        LOGGER.info("Setting the proxy system properties");
        System.setProperty("http.proxyHost", "192.168.41.8");
        System.setProperty("http.proxyProxy", "80");
    }

    @Test
    public void withProxyTest() {
        Assert.assertTrue(template.getRequestFactory() instanceof SimpleClientHttpRequestFactory);
    }

    @AfterClass
    public static void unsetSystemProps() {
        LOGGER.info("UN-setting the proxy system properties");
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyProxy");
    }
}
