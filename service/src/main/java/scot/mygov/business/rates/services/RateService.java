package scot.mygov.business.rates.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import scot.mygov.business.rates.representations.LocalAuthority;
import scot.mygov.business.rates.representations.LocalAuthorityLinks;
import scot.mygov.business.rates.representations.Property;
import scot.mygov.business.rates.representations.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

@Service
public class RateService {

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

    public SearchResponse search(String address) {
        // Retrieve a list of properties from the SAA feed
        SearchResponse searchResponse = saaTemplate.getForObject(saaUrl, SearchResponse.class, address, saaKey);
        if (searchResponse.getProperties() == null) {
            return searchResponse;
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

}
