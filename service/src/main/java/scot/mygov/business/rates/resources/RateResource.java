package scot.mygov.business.rates.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import scot.mygov.business.rates.representations.SearchResponse;
import scot.mygov.business.rates.services.RateService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/address")
public class RateResource {

    private static final Logger LOG = LoggerFactory.getLogger(RateResource.class);

    private final RateService rates;

    @Autowired
    public RateResource(RateService rates) throws IOException {
        this.rates = rates;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Cacheable("saa.search")
    public SearchResponse search(@RequestParam(value = "search", required = true) String search) {
        String postcode = urlSafe(search);

        SearchResponse searchResponse = rates.search(postcode);
        // Since the client is expecting a 404 when no properties are found, we now need to check for an empty list.
        if (searchResponse.getProperties() == null || searchResponse.getProperties().isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "No properties found.");
        }

        return rates.search(postcode);
    }

    /**
     * The SAA search requires the address to be + separated, not \n separated.
     * They supply the addresses with \n so to save the web client from replacing
     * the characters, this does it here.
     *
     * @param search The client requested search
     * @return The search but with \n replaced with +
     * @throws UnsupportedEncodingException
     */
    protected String urlSafe(String search) {
        if (StringUtils.isEmpty(search)) {
            return "";
        } else {
            String cleaned = search.replace("\\n", " ");
            LOG.debug("Searching using " + cleaned);
            return cleaned;
        }
    }
}
