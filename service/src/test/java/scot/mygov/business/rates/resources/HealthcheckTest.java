package scot.mygov.business.rates.resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import scot.mygov.business.rates.representations.SearchResponse;
import scot.mygov.business.rates.services.RateService;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthcheckTest {

    private Healthcheck healthcheck;

    private RateService rateService;

    @Before
    public void setUp() throws IOException {
        rateService = mock(RateService.class);
        healthcheck = new Healthcheck(rateService, "EH66QQ");
    }

    @Test
    public void returns200WhenHealthy() throws IOException {
        SearchResponse saaResponse = loadResponse("/victoria-quay.json");
        when(rateService.search(anyString())).thenReturn(saaResponse);

        ResponseEntity<String> health = healthcheck.health();

        assertEquals(200, health.getStatusCode().value());
    }

    @Test
    public void returns503WhenUnavailable() throws IOException {
        SearchResponse saaResponse = loadResponse("/victoria-quay.json");
        when(rateService.search("EH66QQ")).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ResponseEntity<String> health = healthcheck.health();

        assertEquals(503, health.getStatusCode().value());
    }

    private SearchResponse loadResponse(String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SearchResponse saaResponse;
        try (InputStream is = getClass().getResourceAsStream(file)) {
            saaResponse = mapper.readValue(is, SearchResponse.class);
        }
        return saaResponse;
    }

}
