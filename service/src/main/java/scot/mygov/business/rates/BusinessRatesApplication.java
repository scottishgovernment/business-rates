package scot.mygov.business.rates;

import jakarta.ws.rs.core.Application;
import scot.mygov.business.rates.resources.RateResource;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class BusinessRatesApplication extends Application {

    @Inject
    RateResource rateResource;

    @Inject
    ErrorHandler errorHandler;

    @Inject
    Healthcheck healthcheck;

    @Inject
    RequestLogger logger;

    @Inject
    public BusinessRatesApplication() {
        // Default constructor
    }

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(asList(
                rateResource,
                errorHandler,
                healthcheck,
                logger
        ));
    }

}
