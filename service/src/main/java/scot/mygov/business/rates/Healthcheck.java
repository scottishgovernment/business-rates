package scot.mygov.business.rates;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import javax.inject.Inject;

@Path("")
public class Healthcheck {

    private final SearchMonitor searchMonitor;

    @Inject
    public Healthcheck(SearchMonitor searchMonitor) {
        this.searchMonitor = searchMonitor;
    }

    @GET
    @Path("up")
    public Response up() {
        return toResponse(true);
    }

    @GET
    @Path("health")
    public Response health() {
        boolean ok = searchMonitor.isHealthy();
        return toResponse(ok);
    }

    private Response toResponse(boolean ok) {
        if (!ok) {
            return Response
                    .status(Status.SERVICE_UNAVAILABLE)
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .entity("Unavailable\n")
                    .build();
        }
        return Response
                .status(Status.OK)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .entity("OK\n")
                .build();
    }

}
