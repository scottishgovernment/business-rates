package scot.mygov.business.rates;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class RequestLogger implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessRates.class);

    private static final String START_PROPERTY = "start";

    private static final String URL_QUERY = "url.query";

    @Inject
    public RequestLogger() {
        // Default constructor
    }

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        Instant start = Instant.now();
        request.setProperty(START_PROPERTY, start);

        MDC.put("url.path", request.getUriInfo().getPath());
        MDC.put("http.request.method", request.getRequest().getMethod());

        String query = request.getUriInfo().getRequestUri().getQuery();
        if (query != null) {
            MDC.put(URL_QUERY, query);
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (!"/health".equals(request.getUriInfo().getPath())) {
            logRequest(request, response);
        }
        MDC.remove(URL_QUERY);
    }

    private static void logRequest(ContainerRequestContext request, ContainerResponseContext response) {
        Instant start = (Instant) request.getProperty(START_PROPERTY);
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();

        LOGGER.atInfo()
                .addKeyValue("event.start", start.toString())
                .addKeyValue("event.end", end.toString())
                .addKeyValue("event.duration", Long.toString(duration))
                .addKeyValue("event.outcome", outcome(response.getStatusInfo().getFamily()))
                .addKeyValue("http.response.status_code", Integer.toString(response.getStatus()))
                .addArgument(response.getStatus())
                .addArgument(request.getRequest().getMethod())
                .addArgument(request.getUriInfo().getPath())
                .log("{} {} {}");
    }

    static String outcome(Response.Status.Family family) {
        return switch (family) {
            case SUCCESSFUL -> "success";
            case SERVER_ERROR -> "failure";
            case CLIENT_ERROR -> "failure";
            default -> "unknown";
        };
    }

}
