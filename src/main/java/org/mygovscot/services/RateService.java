package org.mygovscot.services;

import java.io.UnsupportedEncodingException;

import org.mygovscot.representations.LocalAuthority;
import org.mygovscot.representations.Postcode;
import org.mygovscot.representations.Property;
import org.mygovscot.representations.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RestTemplate template;

    @Value("${saa.url}")
    private String saaUrl;

    @Value("${geo-search.url}")
    private String geoUrl;

    @RequestMapping(method = RequestMethod.GET)
    @Cacheable("saa.search")
    public SearchResponse search(@RequestParam(value = "search", required = true) String search) {

        SearchResponse searchResponse = template.getForObject(saaUrl, SearchResponse.class, urlSafe(search));

        for (Property property : searchResponse.getProperties()) {
            property.setLocalAuthority(getLocalAuthority(property.getAddress()));
        }

        return searchResponse;
    }

    @RequestMapping(value = "authority", method = RequestMethod.GET)
    @Cacheable("authority.search")
    public LocalAuthority getLocalAuthority(@RequestParam(value = "address", required = true) String address) {

        String postcode = getPostcode(address);
        Postcode code = template.getForObject(geoUrl, Postcode.class, postcode);

        LOG.debug("For postcode {} found LA: {}", postcode, code);

        if (code != null) {
            return code.getLocalAuthority();
        } else {
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
     * They supply the addresses with \n so to save the client from replacing
     * the characters, this does it here.
     * 
     * @param search
     *            The client requested search
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

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unable to process the request.")
    @ExceptionHandler(HttpClientErrorException.class)
    public void forbidden() {
        // Nothing to do
    }

}
