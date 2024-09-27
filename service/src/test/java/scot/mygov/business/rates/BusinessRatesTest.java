package scot.mygov.business.rates;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import org.junit.Test;
import scot.mygov.config.Configuration;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BusinessRatesTest {

    @Test
    public void canCreateDependencyGraph() {
        DaggerBusinessRatesTest_TestMain.create().main();
    }

    @Singleton
    @Component(modules = {
            BusinessRatesModule.class,
            TestConfigurationModule.class,
    })
    interface TestMain {
        BusinessRates main();
    }

    @Module(includes = BusinessRatesModule.class)
    static class TestConfigurationModule {
        @Provides
        @Singleton
        BusinessRatesConfiguration configuration() {
            try {
                BusinessRatesConfiguration config = new BusinessRatesConfiguration();
                Map<String, String> overrides = new HashMap<>();
                overrides.put("saa_url", "http://saa");
                overrides.put("saa_key", "key");
                return Configuration.create(config, null, overrides).getConfiguration();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
