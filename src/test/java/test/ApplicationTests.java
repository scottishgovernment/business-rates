package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mygovscot.Application;
import org.mygovscot.representations.SearchResponse;
import org.mygovscot.services.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    @Ignore
    public void search() {

        // Retrieve the content item from the REST endpoint
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("search", "EH66QQ");
        ResponseEntity<SearchResponse> singleResponse = restTemplate.getForEntity(context, SearchResponse.class, parameters);
        SearchResponse body = singleResponse.getBody();

        // // Make sure the the retrieved content item has the same values as
        // the
        // // saved version
        // Assert.assertEquals(id, ReflectionTestUtils.getField(body, "id"));
        // Assert.assertEquals(type.getName(),
        // body.getContentItemType().getName());

    }

    @Before
    public void setup() {
        context = "http://localhost:9990/address";

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/json"));
        converter.setObjectMapper(mapper);

        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(converter);
    }
}
