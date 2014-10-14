package org.mygovscot.services;

import java.net.InetSocketAddress;
import java.net.Proxy;

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
public class ProxyConfig {

    @Value("${http.proxyHost}")
    private String proxyHost = null;

    @Value("${http.proxyPort}")
    private int proxyPort = 80;

    @Bean
    RestTemplate template() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/json,application/octet-stream"));
        converter.setObjectMapper(mapper);

        RestTemplate restTemplate;

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
