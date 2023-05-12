package scot.mygov.business.rates;

import dagger.Module;
import dagger.Provides;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.business.rates.client.JCacheClient;
import scot.mygov.business.rates.client.SAAClient;
import scot.mygov.business.rates.client.SAAJaxRsClient;
import scot.mygov.business.rates.resources.LocalAuthorities;
import scot.mygov.config.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.time.Clock;

import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.cache.expiry.Duration.ONE_HOUR;

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
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ResteasyClientBuilder.newBuilder();
        return builder
                .connectionPoolSize(10)
                .connectionCheckoutTimeout(3, SECONDS)
                .connectTimeout(3, SECONDS)
                .connectionTTL(10, SECONDS)
                .readTimeout(3, SECONDS)
                .build();
    }

    @Provides
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Provides
    @Singleton
    public CachingProvider cachingProvider() {
        return Caching.getCachingProvider();
    }

    @Provides
    @Singleton
    @Named("service")
    public SAAClient service(CachingProvider cachingProvider, SAAJaxRsClient client) {
        return JCacheClient.create(cachingProvider, client, "service", ONE_HOUR);
    }

    @Provides
    @Singleton
    @Named("healthcheck")
    public SAAClient healthcheck(SAAJaxRsClient client) {
        return client;
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
