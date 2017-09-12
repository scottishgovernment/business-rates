package scot.mygov.business.rates.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import scot.mygov.business.rates.representations.SearchResponse;
import scot.mygov.business.rates.services.RateService;

@RestController
@RequestMapping("/health")
public class Healthcheck {

    private static final Logger LOG = LoggerFactory.getLogger(Healthcheck.class);

    private final RateService rates;

    private final String search;

    @Autowired
    public Healthcheck(
            RateService rates,
            @Value("${health.search}") String search) {
        this.rates = rates;
        this.search = search;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> health() {
        boolean ok;
        try {
            SearchResponse response = rates.search(search);
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
