package scot.mygov.business.rates.resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import scot.mygov.business.rates.BusinessRatesConfiguration;
import scot.mygov.business.rates.representations.SearchResponse;
import scot.mygov.business.rates.services.RateService;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthcheckTest {

    private Healthcheck healthcheck;

    private RateService rateService;

    private BusinessRatesConfiguration.Health config;

    @Before
    public void setUp() throws IOException {
        rateService = mock(RateService.class);
        config = new BusinessRatesConfiguration.Health();
        config.setSearch("EH66QQ");
        healthcheck = new Healthcheck(rateService, config, Clock.systemDefaultZone());
    }

    @Test
    public void returns200WhenHealthy() throws IOException {
        SearchResponse saaResponse = loadResponse("/victoria-quay.json");
        when(rateService.search(anyString())).thenReturn(saaResponse);

        Response health = healthcheck.health();

        assertThat(health.getStatus()).isEqualTo(200);
    }

    @Test
    public void returns503WhenUnavailable() throws IOException {
        SearchResponse saaResponse = loadResponse("/victoria-quay.json");
        when(rateService.search("EH66QQ")).thenReturn(new SearchResponse());

        Response health = healthcheck.health();

        assertThat(health.getStatus()).isEqualTo(503);
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
