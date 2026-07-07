package scot.mygov.business.rates;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchMonitor implements ContainerResponseFilter {

    private final AtomicBoolean lastRequestSuccessful = new AtomicBoolean(true);

    @Inject
    public SearchMonitor() {
        // Default constructor
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        addRequestStatus(responseContext.getStatusInfo());
    }

    public void addRequestStatus(Response.StatusType statusInfo) {
        boolean isOk = statusInfo.getFamily() == Response.Status.Family.SUCCESSFUL;
        lastRequestSuccessful.set(isOk);
    }

    public boolean isHealthy() {
        return lastRequestSuccessful.get();
    }

}
