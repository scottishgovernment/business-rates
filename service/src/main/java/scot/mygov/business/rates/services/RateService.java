package scot.mygov.business.rates.services;

import scot.mygov.business.rates.representations.LocalAuthority;
import scot.mygov.business.rates.representations.LocalAuthorityLinks;
import scot.mygov.business.rates.representations.Property;
import scot.mygov.business.rates.representations.ResultType;
import scot.mygov.business.rates.representations.SearchResponse;

import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

public class RateService {

    private LocalAuthorities authorities;

    private final WebTarget saa;

    @Inject
    public RateService(
            LocalAuthorities authorities,
            WebTarget saa) {
        this.authorities = authorities;
        this.saa = saa;
    }

    public SearchResponse search(String address) {
        Response response = saa.resolveTemplate("search", address)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
        SAAResponse saaResponse = response.readEntity(SAAResponse.class);
        return convert(saaResponse, response.getStatus());
    }

    private SearchResponse convert(SAAResponse response, int statusCode) {
        SearchResponse result = new SearchResponse();
        result.setResultType(ResultType.of(statusCode));
        result.setMessage(response.getMessage());
        if (response.getProperties() != null) {
            result.setProperties(convert(response.getProperties()));
        }
        return result;
    }

    private List<Property> convert(List<SAAProperty> properties) {
        List<Property> result = new ArrayList<>(properties.size());
        for (SAAProperty saaProperty : properties) {
            Property property = new Property();
            property.setRv(saaProperty.getRv());
            property.setAddress(saaProperty.getAddress());
            property.setOccupier(saaProperty.getOccupier());

            int authorityId = saaProperty.getUa();
            property.setUa(authorityId);
            LA la = authorities.getAuthority(authorityId);
            LocalAuthority authority = convert(la);
            property.setLocalAuthority(authority);
            result.add(property);
        }
        sort(result, (p1, p2) -> p1.getAddress().compareTo(p2.getAddress()));
        return result;
    }

    private LocalAuthority convert(LA la) {
        LocalAuthority authority = new LocalAuthority();
        authority.setId(String.valueOf(la.getId()));
        authority.setName(la.getName());
        LocalAuthorityLinks links = new LocalAuthorityLinks();
        authority.setLinks(links);
        links.setTax(la.getLink());
        return authority;
    }

}
