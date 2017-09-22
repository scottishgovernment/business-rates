package scot.mygov.business.rates.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.business.rates.BusinessRatesConfiguration;
import scot.mygov.business.rates.representations.SearchResponse;
import scot.mygov.business.rates.services.RateService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Path("/health")
public class Healthcheck {

    private static final Logger LOG = LoggerFactory.getLogger(Healthcheck.class);

    private final RateService rates;

    private final String search;

    private final Clock clock;

    private final AtomicReference<CachedStatus> cache;

    private final Duration cacheTime;

    @Inject
    public Healthcheck(
            RateService rates,
            BusinessRatesConfiguration.Health config,
            Clock clock) {
        this.rates = rates;
        this.search = config.getSearch();
        this.clock = clock;
        this.cache = new AtomicReference<>();
        this.cacheTime = Duration.parse(config.getCacheTime());
    }

    @GET
    public Response health() {
        boolean ok = getHealth();
        return toResponse(ok);
    }

    private boolean getHealth() {
        Instant now = this.clock.instant();
        boolean ok;
        CachedStatus cached = cache.get();
        if (cached != null && cached.getTime().plus(cacheTime).isAfter(now)) {
            long elapsedMillis = Duration.between(cached.getTime(), now).toMillis();
            LOG.debug("Using cached health (age: {}s)", String.format("%.2f", elapsedMillis / 1000.0));
            ok = cached.isOk();
        } else {
            LOG.info("Checking API health");
            ok = isOk();
            if (cached != null && cached.isOk() != ok) {
                LOG.info("SAA API health is now " + (ok ? "OK" : "unavailable"));
            }
            cache.set(new CachedStatus(now, ok));
        }
        return ok;
    }

    private boolean isOk() {
        boolean ok;
        try {
            SearchResponse response = rates.search(search);
            ok = !response.getProperties().isEmpty();
        } catch (Exception ex) {
            ok = false;
            LOG.trace("Search for healthcheck failed", ex);
        }
        return ok;
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

    static class CachedStatus {

        private final Instant time;

        private final boolean ok;

        public CachedStatus(Instant time, boolean ok) {
            this.time = time;
            this.ok = ok;
        }

        public Instant getTime() {
            return time;
        }

        public boolean isOk() {
            return ok;
        }

    }

}
