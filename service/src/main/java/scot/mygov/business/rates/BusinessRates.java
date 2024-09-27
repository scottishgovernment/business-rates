package scot.mygov.business.rates;

import dagger.Component;
import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;

public class BusinessRates {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(BusinessRates.class);

    @Inject
    BusinessRatesConfiguration config;

    @Inject
    BusinessRatesApplication app;

    @Inject
    public BusinessRates() {
        // Default constructor
    }

    public static final void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        BusinessRates businessRates = DaggerBusinessRates_Main.create().main();
        businessRates.run();
    }

    public void run() {
        Server server = new Server();
        server.deploy(app);
        server.start(Undertow.builder().addHttpListener(config.getPort(), "::"));
        LOGGER.info("Listening on port {}", server.port());
    }

    public static class Server extends UndertowJaxrsServer {
        public int port() {
            InetSocketAddress address = (InetSocketAddress) server
                    .getListenerInfo()
                    .get(0)
                    .getAddress();
            return address.getPort();
        }
    }

    @Singleton
    @Component(modules = {
            BusinessRatesModule.class,
            EnvironmentConfigurationModule.class,
    })
    interface Main {
        BusinessRates main();
    }

}
