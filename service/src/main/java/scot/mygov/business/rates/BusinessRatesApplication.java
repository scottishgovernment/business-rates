package scot.mygov.business.rates;

import scot.mygov.business.rates.resources.Healthcheck;
import scot.mygov.business.rates.resources.RateResource;

import javax.inject.Inject;
import javax.ws.rs.core.Application;
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
