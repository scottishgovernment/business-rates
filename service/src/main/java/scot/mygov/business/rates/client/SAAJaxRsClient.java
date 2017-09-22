package scot.mygov.business.rates.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class SAAJaxRsClient implements SAAClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SAAJaxRsClient.class);

    private final WebTarget saa;

    @Inject
    public SAAJaxRsClient(WebTarget saa) {
        this.saa = saa;
    }

    public SAAResponse search(String address) {
        LOGGER.info("Query: {}", address);

        Response response = saa.resolveTemplate("search", address)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        SAAResult result = response.readEntity(SAAResult.class);
        ResultType type = ResultType.of(response.getStatus());

        List<SAAProperty> properties = result.getProperties();
        LOGGER.info("Result: {} ({} properties)",
                type,
                properties != null ? properties.size() : 0);

        return new SAAResponse(type, result);
    }

}
