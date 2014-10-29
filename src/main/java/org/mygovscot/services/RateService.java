package org.mygovscot.services;

import java.io.UnsupportedEncodingException;

import org.mygovscot.representations.LocalAuthority;
import org.mygovscot.representations.LocalAuthorityLinks;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(RateService.class);

    @Autowired
    private RestTemplate template;

    @Value("${saa.url}")
    private String saaUrl;

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
        LocalAuthority sampleAuthority = new LocalAuthority("S100000");
        sampleAuthority.setCode(300);
        sampleAuthority.setName("Edinburgh Village");

        LocalAuthorityLinks authorityLinks = new LocalAuthorityLinks();
        authorityLinks.setHomepage("http://www.edinburgh.gov.uk/");
        authorityLinks.setTax("http://www.edinburgh.gov.uk/info/20020/business_rates/757/non-domestic_business_rates_charges");

        sampleAuthority.setLinks(authorityLinks);

        return sampleAuthority;
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
            LOGGER.debug("Searching using " + cleaned);
            return cleaned;
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unable to process the request.")
    @ExceptionHandler(HttpClientErrorException.class)
    public void forbidden() {
        // Nothing to do
    }

}
