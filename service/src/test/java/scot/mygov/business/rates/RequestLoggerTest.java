package scot.mygov.business.rates;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestLoggerTest {

    @Test
    public void successfulResponseHasSuccessfulOutcome() {
        assertThat(RequestLogger.outcome(Response.Status.Family.SUCCESSFUL)).isEqualTo("success");
    }

    @Test
    public void clientErrorHasFailureOutcome() {
        assertThat(RequestLogger.outcome(Response.Status.Family.CLIENT_ERROR)).isEqualTo("failure");
    }

    @Test
    public void serverErrorHasFailureOutcome() {
        assertThat(RequestLogger.outcome(Response.Status.Family.SERVER_ERROR)).isEqualTo("failure");
    }

    @Test
    public void otherResponseHasUnknownOutcome() {
        assertThat(RequestLogger.outcome(Response.Status.Family.INFORMATIONAL)).isEqualTo("unknown");
    }

}
