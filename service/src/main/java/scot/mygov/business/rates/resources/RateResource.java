package scot.mygov.business.rates.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.business.rates.client.ResultType;
import scot.mygov.business.rates.client.SAAClient;
import scot.mygov.business.rates.client.SAAProperty;
import scot.mygov.business.rates.client.SAAResponse;
import scot.mygov.business.rates.client.SAAResult;
import scot.mygov.business.rates.representations.LocalAuthority;
import scot.mygov.business.rates.representations.LocalAuthorityLinks;
import scot.mygov.business.rates.representations.Property;
import scot.mygov.business.rates.representations.SearchResponse;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

@Path("address")
public class RateResource {

    private static final Logger LOG = LoggerFactory.getLogger(RateResource.class);

    private final SAAClient rates;

    private final LocalAuthorities authorities;

    @Inject
    public RateResource(
            @Named("service") SAAClient rates,
            LocalAuthorities authorities)  {
        this.rates = rates;
        this.authorities = authorities;
    }

    @GET
    public Response search(@QueryParam("search") String search) {
        String postcode = urlSafe(search);
        SAAResponse searchResponse = rates.search(postcode);

        ResultType type = searchResponse.getType();
        SAAResult properties = searchResponse.getResult();

        SearchResponse response = new SearchResponse();
        response.setResultType(type);
        response.setMessage(properties.getMessage());
        if (properties.getProperties() != null) {
            response.setProperties(convert(properties.getProperties()));
        }
        return Response.status(Status.OK)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(response)
                .build();
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
        if (search == null) {
            return "";
        }
        String cleaned = search.replace("\\n", " ");
        LOG.debug("Searching using " + cleaned);
        return cleaned;
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
