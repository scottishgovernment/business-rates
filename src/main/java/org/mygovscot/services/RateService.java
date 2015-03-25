package org.mygovscot.services;

import org.mygovscot.representations.LocalAuthority;
import org.mygovscot.representations.Postcode;
import org.mygovscot.representations.Property;
import org.mygovscot.representations.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    private Consumer<Property> action;

    @RequestMapping(method = RequestMethod.GET)
    @Cacheable("saa.search")
    public SearchResponse search(@RequestParam(value = "search", required = true) String search) {

        // Retrieve a list of properties from the SAA feed
        SearchResponse searchResponse = saaTemplate.getForObject(saaUrl, SearchResponse.class, urlSafe(search));

        // Populate the local authority from our geo-search service
        searchResponse.getProperties().parallelStream().forEach(property -> {
            LocalAuthority authority = getLocalAuthority(property.getAddress());
            if (authority != null) {
                property.setLocalAuthority(authority);
            }
        });

        // Filter the list of properties for any that don't have local authorities
        searchResponse.setProperties(
                searchResponse.getProperties().parallelStream()
                        .filter(p -> p.getLocalAuthority() != null)
                        .sorted((p1, p2) -> p1.getAddress().compareTo(p2.getAddress()))
                        .collect(Collectors.toList()));

        return searchResponse;
    }

    @RequestMapping(value = "authority", method = RequestMethod.GET)
    @Cacheable("authority.search")
    public LocalAuthority getLocalAuthority(@RequestParam(value = "address", required = true) String address) {

        try {
            String postcode = getPostcode(address);

            Postcode code = geoSearchTemplate.getForObject(geoUrl, Postcode.class, postcode);
            LOG.debug("For postcode {} found LA: {}", postcode, code);

            if (code != null) {
                return code.getLocalAuthority();
            } else {
                return null;
            }
        } catch (HttpClientErrorException e) {
            LOG.error("Unable to retrieve local authority details for address {} [{}]", address, e.getLocalizedMessage());
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
}
