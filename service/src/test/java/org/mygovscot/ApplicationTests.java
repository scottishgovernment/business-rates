package org.mygovscot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mygovscot.representations.SearchResponse;
import org.mygovscot.services.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port=9990")
public class ApplicationTests {

    private String context;

    private RestTemplate restTemplate;

    @Autowired
    private RateService rateService;

    @Test
    public void search() {
        // Retrieve the content item from the REST endpoint
        ResponseEntity<SearchResponse> singleResponse = restTemplate.getForEntity(context, SearchResponse.class, "EH66QQ");
        SearchResponse body = singleResponse.getBody();

        assertEquals(HttpStatus.OK, singleResponse.getStatusCode());
        assertTrue(body.getProperties().size() > 0);

    }

    @Before
    public void setup() {
        context = "http://localhost:9990/address/?search={search}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/json"));
        converter.setObjectMapper(mapper);

        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(converter);
    }

    @Test(expected = HttpClientErrorException.class)
    public void badServiceTest() {
        ResponseEntity<String> singleResponse = restTemplate.getForEntity(context, String.class, "EH1");
        String body = singleResponse.getBody();

        assertEquals(HttpStatus.FORBIDDEN, singleResponse.getStatusCode());
    }

    @Test(expected = HttpClientErrorException.class)
    public void badAddressTest() {
        ResponseEntity<String> singleResponse = restTemplate.getForEntity(context, String.class, "THISADDRESSDOESNOTEXIST");
        String body = singleResponse.getBody();

        assertEquals(HttpStatus.NOT_FOUND, singleResponse.getStatusCode());
    }

    @Test
    public void badAddressTest2() {
        ResponseEntity<String> singleResponse = restTemplate.getForEntity(context, String.class, "high street glasgow");
        String body = singleResponse.getBody();

        assertEquals(HttpStatus.OK, singleResponse.getStatusCode());
    }
}
