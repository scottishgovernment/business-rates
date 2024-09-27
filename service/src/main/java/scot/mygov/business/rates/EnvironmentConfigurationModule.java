package scot.mygov.business.rates;

import dagger.Module;
import dagger.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.config.Configuration;

import javax.inject.Singleton;

@Module
public class EnvironmentConfigurationModule {

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

}
