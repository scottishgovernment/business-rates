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
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.entries;

public class RequestLogger implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessRates.class);

    private static final String START_PROPERTY = "start";

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
            MDC.put("url.query", query);
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response)
            throws IOException {
        Instant start = (Instant) request.getProperty(START_PROPERTY);
        Instant end = Instant.now();

        Map<String, String> event = new HashMap<>();
        event.put("event.start", start.toString());
        event.put("event.end", end.toString());
        event.put("event.duration", Long.toString(Duration.between(start, end).toMillis()));
        event.put("event.outcome", outcome(response.getStatusInfo().getFamily()));

        event.put("http.response.status_code", Integer.toString(response.getStatus()));

        String method = request.getRequest().getMethod();
        String path = request.getUriInfo().getPath();
        int status = response.getStatus();
        LOGGER.info("{} {} {}",
                status,
                method,
                path,
                entries(event));
    }

    static String outcome(Response.Status.Family family) {
        switch (family) {
            case SUCCESSFUL:
                return "success";
            case SERVER_ERROR:
                return "failure";
            case CLIENT_ERROR:
                return "failure";
            default:
                return "unknown";
        }
    }

}
