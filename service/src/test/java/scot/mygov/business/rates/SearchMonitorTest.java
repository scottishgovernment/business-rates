package scot.mygov.business.rates;

import jakarta.ws.rs.core.Response;
import org.junit.Test;
import scot.mygov.business.rates.resources.RateResource;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchMonitorTest {

    private final SearchMonitor searchMonitor = new SearchMonitor();

    @Test
    public void freshMonitorIsHealthy() {
        assertThat(searchMonitor.isHealthy()).isTrue();
    }

    @Test
    public void becomesUnhealthyAfterServerError() {
        searchMonitor.addRequestStatus(Response.Status.INTERNAL_SERVER_ERROR);
        assertThat(searchMonitor.isHealthy()).isFalse();
    }

    @Test
    public void recoversAfterSubsequentSuccess() {
        searchMonitor.addRequestStatus(Response.Status.INTERNAL_SERVER_ERROR);
        searchMonitor.addRequestStatus(Response.Status.OK);
        assertThat(searchMonitor.isHealthy()).isTrue();
    }

    @Test
    public void isBoundToSearchRequests() {
        assertThat(SearchMonitor.class.isAnnotationPresent(SearchRequest.class)).isTrue();
    }

    @Test
    public void requestLoggerIsBoundToSearchRequests() {
        assertThat(RequestLogger.class.isAnnotationPresent(SearchRequest.class)).isTrue();
    }

    @Test
    public void searchMethodIsBoundToSearchRequests() throws NoSuchMethodException {
        assertThat(RateResource.class.getMethod("search", String.class)
                .isAnnotationPresent(SearchRequest.class)).isTrue();
    }

}
