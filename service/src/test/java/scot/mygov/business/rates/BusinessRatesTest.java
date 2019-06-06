package scot.mygov.business.rates;

import dagger.Module;
import dagger.ObjectGraph;
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
        ObjectGraph graph = ObjectGraph.create(new TestBusinessRatesModule());
        graph.get(BusinessRates.class);
    }

    @Module(includes = BusinessRatesModule.class, injects = BusinessRates.class, overrides = true)
    static class TestBusinessRatesModule {
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
