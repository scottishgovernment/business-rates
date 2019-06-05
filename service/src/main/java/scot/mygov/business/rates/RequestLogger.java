package scot.mygov.business.rates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
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

        event.put("url.path", request.getUriInfo().getPath());

        event.put("http.request.method", request.getRequest().getMethod());
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
