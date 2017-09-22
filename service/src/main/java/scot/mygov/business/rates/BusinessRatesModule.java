package scot.mygov.business.rates;

import dagger.Module;
import dagger.Provides;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.business.rates.services.LocalAuthorities;
import scot.mygov.config.Configuration;

import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.time.Clock;

@Module(injects = BusinessRates.class)
public class BusinessRatesModule {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(BusinessRatesModule.class);

    private static final String APP_NAME = "business_rates";

    @Provides
    @Singleton
    BusinessRatesConfiguration configuration() {
        Configuration<BusinessRatesConfiguration> configuration = Configuration
                .load(new BusinessRatesConfiguration(), APP_NAME)
                .validate();
        LOGGER.info("{}", configuration);
        return configuration.getConfiguration();
    }

    @Provides
    @Singleton
    BusinessRatesConfiguration.Health healthConfig(BusinessRatesConfiguration configuration) {
        return configuration.getHealth();
    }

    @Provides
    @Singleton
    WebTarget saa(Client client, BusinessRatesConfiguration config) {
        BusinessRatesConfiguration.SAA saa = config.getSAA();
        UriBuilder uriBuilder = UriBuilder
                .fromUri(saa.getUrl())
                .resolveTemplate("key", saa.getKey());
        return client.target(uriBuilder);
    }

    @Provides
    @Singleton
    Client client() {
        return new ResteasyClientBuilder()
                .connectionPoolSize(10)
                .build();
    }

    @Provides
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Provides
    @Singleton
    LocalAuthorities localAuthorities() {
        LocalAuthorities localAuthorities = new LocalAuthorities();
        try {
            localAuthorities.load();
        } catch (IOException ex) {
            throw new RuntimeException("Could not load local authority data", ex);
        }
        return localAuthorities;
    }

}
