package org.mygovscot.config;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ProxyConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyConfiguration.class);

    @Value("${saa.proxyHost}")
    private String saaProxyHost = null;

    @Value("${saa.proxyPort}")
    private int saaProxyPort = 80;

    @Value("${geo_search.proxyHost}")
    private String geoSearchProxyHost = null;

    @Value("${geo_search.proxyPort}")
    private int geoSearchProxyPort = 80;

    @Bean(name = "saaRestTemplate")
    RestTemplate saaRestTemplate() {
        return getRestTemplate(saaProxyHost, saaProxyPort);
    }

    @Bean(name = "geoSearchRestTemplate")
    RestTemplate postcodeRestTemplate() {
        return getRestTemplate(geoSearchProxyHost, geoSearchProxyPort);
    }

    private RestTemplate getRestTemplate(String proxyHost, int proxyPort) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/json,application/octet-stream"));
        converter.setObjectMapper(mapper);

        RestTemplate restTemplate;

        LOG.info("Proxy configuration = " + proxyHost + ":" + proxyPort);

        if (StringUtils.isEmpty(proxyHost)) {
            restTemplate = new RestTemplate();
        } else {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setProxy(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyHost, proxyPort)));

            restTemplate = new RestTemplate(factory);
        }

        restTemplate.getMessageConverters().add(converter);

        return restTemplate;
    }
}