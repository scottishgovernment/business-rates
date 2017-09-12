package scot.mygov.business.rates.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class ProxyConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyConfiguration.class);

    @Value("${saa.proxyHost}")
    private String saaProxyHost = null;

    @Value("${saa.proxyPort}")
    private int saaProxyPort = 80;

    @Value("${saa.username}")
    private String username = null;

    @Value("${saa.password}")
    private String password = null;

    @Bean(name = "saaRestTemplate")
    RestTemplate saaRestTemplate() {
        return getRestTemplate(saaProxyHost, saaProxyPort);
    }

    private RestTemplate getRestTemplate(String proxyHost, int proxyPort) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/json,application/octet-stream"));
        converter.setObjectMapper(mapper);

        RestTemplate restTemplate;

        LOG.info("Proxy configuration = " + proxyHost + ":" + proxyPort);

        SimpleClientHttpRequestFactory requestFactory =
                new SAAClientHttpRequestFactory(username, password);
        if (!StringUtils.isEmpty(proxyHost)) {
            requestFactory.setProxy(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyHost, proxyPort)));
        }

        restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }

}
