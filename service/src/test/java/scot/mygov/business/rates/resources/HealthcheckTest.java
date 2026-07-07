package scot.mygov.business.rates.resources;

import jakarta.ws.rs.core.Response;
import org.junit.Test;
import scot.mygov.business.rates.Healthcheck;
import scot.mygov.business.rates.SearchMonitor;

import static org.assertj.core.api.Assertions.assertThat;

public class HealthcheckTest {

    private final SearchMonitor searchMonitor = new SearchMonitor();

    private final Healthcheck healthcheck = new Healthcheck(searchMonitor);

    @Test
    public void returns200WhenHealthy() {
        try (Response health = healthcheck.health()) {
            assertThat(health.getStatus()).isEqualTo(200);
        }
    }

    @Test
    public void returns503AfterErrors() {
        searchMonitor.addRequestStatus(Response.Status.INTERNAL_SERVER_ERROR);
        try (Response health = healthcheck.health()) {
            assertThat(health.getStatus()).isEqualTo(503);
        }
    }

}
