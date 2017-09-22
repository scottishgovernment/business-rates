package scot.mygov.business.rates.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.business.rates.representations.SearchResponse;
import scot.mygov.business.rates.services.RateService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;

@Path("address")
public class RateResource {

    private static final Logger LOG = LoggerFactory.getLogger(RateResource.class);

    private final RateService rates;

    @Inject
    public RateResource(RateService rates)  {
        this.rates = rates;
    }

    @GET
    public Response search(@QueryParam("search") String search) {
        String postcode = urlSafe(search);

        SearchResponse searchResponse = rates.search(postcode);
        int statusCode = searchResponse.getResultType().getStatusCode();
        return Response.status(statusCode)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(searchResponse)
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

}
