package org.mygovscot.services;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.mygovscot.representations.LocalAuthority;
import org.mygovscot.representations.Postcode;
import org.mygovscot.representations.Property;
import org.mygovscot.representations.SearchResponse;
import org.mygovscot.services.exceptions.AddressNotFoundException;
import org.mygovscot.services.exceptions.TooManyResultsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/address")
public class RateService {

    private static final Logger LOG = LoggerFactory.getLogger(RateService.class);

    @Autowired
    @Qualifier("geoSearchRestTemplate")
    private RestTemplate geoSearchTemplate;

    @Autowired
    @Qualifier("saaRestTemplate")
    private RestTemplate saaTemplate;

    @Value("${saa.url}")
    private String saaUrl;

    @Value("${geo_search.url}")
    private String geoUrl;

    @RequestMapping(method = RequestMethod.GET)
    @Cacheable("saa.search")
    public SearchResponse search(@RequestParam(value = "search", required = true) String search) {

        try {
            SearchResponse searchResponse = saaTemplate.getForObject(saaUrl, SearchResponse.class, urlSafe(search));

            List<Property> validProperties = new LinkedList<Property>();

            for (Property property : searchResponse.getProperties()) {
                LocalAuthority authority = getLocalAuthority(property.getAddress());
                if (authority != null) {
                    property.setLocalAuthority(authority);
                    validProperties.add(property);
                }
            }

            searchResponse.setProperties(validProperties);
            return searchResponse;
        } catch (HttpClientErrorException e) {
            LOG.warn("Problem processing address search: {} ({})", e.getResponseBodyAsString(), e.getStatusCode());
            
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new AddressNotFoundException(e.getResponseBodyAsString());
            }
            
            if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
                throw new TooManyResultsException(e.getResponseBodyAsString());
            }
            
            throw e;
        }
    }

    @RequestMapping(value = "authority", method = RequestMethod.GET)
    @Cacheable("authority.search")
    public LocalAuthority getLocalAuthority(@RequestParam(value = "address", required = true) String address) {

        String postcode = getPostcode(address);
        try {
            Postcode code = geoSearchTemplate.getForObject(geoUrl, Postcode.class, postcode);
            LOG.debug("For postcode {} found LA: {}", postcode, code);

            if (code != null) {
                return code.getLocalAuthority();
            } else {
                return null;
            }
        } catch (HttpClientErrorException e) {
            LOG.warn("Unable to find a local authority for postcode " + postcode, e);
            return null;
        }

    }

    String getPostcode(String address) {
        LOG.debug("Parsing address for postcode {}", address);

        String postcode = null;
        if (address.contains("\n")) {
            String[] addressParts = address.split("\\n");
            postcode = addressParts[addressParts.length - 1];
        } else {
            postcode = address;
        }
        postcode = postcode.replace(" ", "").toUpperCase();

        LOG.debug("Postcode ----{}----", postcode);

        return postcode;
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

    @ExceptionHandler(AddressNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Address not found")
    public String forbidden() {
        return "Address not found";
    }

    @ExceptionHandler(TooManyResultsException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Too many results")
    public String tooManyResults() {
        return "Too many addresses found.";
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unable to process request")
    public String error() {
        return "error.";
    }
}
