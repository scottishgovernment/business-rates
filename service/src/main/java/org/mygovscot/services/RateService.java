package org.mygovscot.services;

import org.mygovscot.representations.LocalAuthority;
import org.mygovscot.representations.LocalAuthorityLinks;
import org.mygovscot.representations.Property;
import org.mygovscot.representations.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

@RestController
@RequestMapping("/address")
public class RateService {

    private static final Logger LOG = LoggerFactory.getLogger(RateService.class);

    @Autowired
    @Qualifier("saaRestTemplate")
    private RestTemplate saaTemplate;

    @Value("${saa.url}")
    private String saaUrl;

    @Value("${saa.key}")
    private String saaKey;

    private LocalAuthorities authorities;

    @Autowired
    public RateService(LocalAuthorities authorities) throws IOException {
        this.authorities = authorities;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Cacheable("saa.search")
    public SearchResponse search(@RequestParam(value = "search", required = true) String search) {

        // Retrieve a list of properties from the SAA feed
        String postcode = urlSafe(search);
        SearchResponse searchResponse = saaTemplate.getForObject(saaUrl, SearchResponse.class, postcode, saaKey);

        // Since the client is expecting a 404 when no properties are found, we now need to check for an empty list.
        if (searchResponse.getProperties() == null || searchResponse.getProperties().isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "No properties found.");
        }

        List<Property> properties = new ArrayList<>();
        for (Property property : searchResponse.getProperties()) {
            int authorityId = property.getUa();
            LA la = authorities.getAuthority(authorityId);
            if (la != null) {
                LocalAuthority authority = convert(la);
                property.setLocalAuthority(authority);
                properties.add(property);
            }
        }

        sort(properties, (p1, p2) -> p1.getAddress().compareTo(p2.getAddress()));
        return searchResponse;
    }

    private static LocalAuthority convert(LA la) {
        LocalAuthority authority = new LocalAuthority();
        authority.setId(String.valueOf(la.getId()));
        authority.setName(la.getName());
        LocalAuthorityLinks links = new LocalAuthorityLinks();
        authority.setLinks(links);
        links.setTax(la.getLink());
        return authority;
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
