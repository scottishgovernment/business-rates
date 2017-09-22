package scot.mygov.business.rates.resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import scot.mygov.business.rates.BusinessRatesConfiguration;
import scot.mygov.business.rates.Healthcheck;
import scot.mygov.business.rates.client.ResultType;
import scot.mygov.business.rates.client.SAAClient;
import scot.mygov.business.rates.client.SAAResponse;
import scot.mygov.business.rates.client.SAAResult;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HealthcheckTest {

    private Healthcheck healthcheck;

    private SAAClient rateService;

    private BusinessRatesConfiguration.Health config;

    private Clock systemClock;

    private Clock clock;

    @Before
    public void setUp() throws IOException {
        rateService = mock(SAAClient.class);
        config = new BusinessRatesConfiguration.Health();
        config.setSearch("EH66QQ");
        systemClock = Clock.systemDefaultZone();
        clock = mock(Clock.class);
        when(clock.instant()).thenAnswer(invocation -> systemClock.instant());
        healthcheck = new Healthcheck(rateService, config, clock);
    }

    @Test
    public void returns200WhenHealthy() throws IOException {
        SAAResponse saaResponse = loadResponse(ResultType.OK, "/victoria-quay.json");
        when(rateService.search(anyString())).thenReturn(saaResponse);

        Response health = healthcheck.health();

        assertThat(health.getStatus()).isEqualTo(200);
    }

    @Test
    public void cachesResult() throws IOException {
        SAAResponse saaResponse = loadResponse(ResultType.OK, "/victoria-quay.json");
        when(rateService.search(anyString())).thenReturn(saaResponse);
        reset(clock);
        Instant time = systemClock.instant();
        when(clock.instant()).thenReturn(time);
        when(clock.instant()).thenReturn(time.plus(5, ChronoUnit.SECONDS));

        Response health1 = healthcheck.health();
        Response health2 = healthcheck.health();

        verify(rateService, only()).search(anyString());
        assertThat(health1.getStatus()).isEqualTo(200);
        assertThat(health2.getStatus()).isEqualTo(200);
    }

    @Test
    public void cacheExpires() throws IOException {
        SAAResponse saaResponse = loadResponse(ResultType.OK, "/victoria-quay.json");
        when(rateService.search(anyString())).thenReturn(saaResponse);
        reset(clock);
        Instant time = systemClock.instant();
        when(clock.instant()).thenReturn(time, time.plus(500, ChronoUnit.SECONDS));

        Response health1 = healthcheck.health();
        Response health2 = healthcheck.health();

        verify(rateService, times(2)).search(anyString());
        assertThat(health1.getStatus()).isEqualTo(200);
        assertThat(health2.getStatus()).isEqualTo(200);
    }


    @Test
    public void identifiesFailure() throws IOException {
        SAAResponse saaResponse = loadResponse(ResultType.OK, "/victoria-quay.json");
        when(rateService.search(anyString()))
                .thenReturn(saaResponse)
                .thenThrow(new ProcessingException("test"));
        reset(clock);
        Instant time = systemClock.instant();
        when(clock.instant()).thenReturn(time, time.plus(500, ChronoUnit.SECONDS));

        Response health1 = healthcheck.health();
        Response health2 = healthcheck.health();

        verify(rateService, times(2)).search(anyString());
        assertThat(health1.getStatus()).isEqualTo(200);
        assertThat(health2.getStatus()).isEqualTo(503);
    }

    @Test
    public void identifiesRecovery() throws IOException {
        SAAResponse saaResponse = loadResponse(ResultType.OK, "/victoria-quay.json");
        when(rateService.search(anyString()))
                .thenThrow(new ProcessingException("test"))
                .thenReturn(saaResponse);
        reset(clock);
        Instant time = systemClock.instant();
        when(clock.instant()).thenReturn(time, time.plus(500, ChronoUnit.SECONDS));

        Response health1 = healthcheck.health();
        Response health2 = healthcheck.health();

        verify(rateService, times(2)).search(anyString());
        assertThat(health1.getStatus()).isEqualTo(503);
        assertThat(health2.getStatus()).isEqualTo(200);
    }

    @Test
    public void returns503WhenUnavailable() throws IOException {
        SAAResponse result = new SAAResponse(ResultType.NO_RESULTS, new SAAResult());
        when(rateService.search("EH66QQ")).thenReturn(result);

        Response health = healthcheck.health();

        assertThat(health.getStatus()).isEqualTo(503);
    }

    private SAAResponse loadResponse(ResultType type, String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SAAResult result;
        try (InputStream is = getClass().getResourceAsStream(file)) {
            result = mapper.readValue(is, SAAResult.class);
        }
        return new SAAResponse(type, result);
    }

}
