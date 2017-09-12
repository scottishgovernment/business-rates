package scot.mygov.business.rates.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import scot.mygov.business.rates.representations.SearchResponse;
import scot.mygov.business.rates.services.RateService;

import java.io.IOException;

@RestController
@RequestMapping("/health")
public class Healthcheck {

    private static final Logger LOG = LoggerFactory.getLogger(Healthcheck.class);

    private final RateService rates;

    @Autowired
    public Healthcheck(RateService rates) throws IOException {
        this.rates = rates;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> health() {
        boolean ok;
        try {
            SearchResponse response = rates.search("EH66sQQ");
            ok = !response.getProperties().isEmpty();
        } catch (HttpClientErrorException ex) {
            ok = false;
            LOG.trace("Search for healthcheck failed", ex);
        }
        if (!ok) {
            return new ResponseEntity<String>("Unavailable\n", HttpStatus.SERVICE_UNAVAILABLE);
        }
        return ResponseEntity.ok("OK\n");
    }

}
