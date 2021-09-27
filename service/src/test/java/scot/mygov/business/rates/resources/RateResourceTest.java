package scot.mygov.business.rates.resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import scot.mygov.business.rates.client.ResultType;
import scot.mygov.business.rates.client.SAAClient;
import scot.mygov.business.rates.client.SAAResponse;
import scot.mygov.business.rates.client.SAAResult;
import scot.mygov.business.rates.representations.Property;
import scot.mygov.business.rates.representations.SearchResponse;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RateResourceTest {

    private SAAClient service;

    private RateResource resource;

    @Before
    public void setUp() throws IOException {
        LocalAuthorities authorities = new LocalAuthorities();
        authorities.load();
        service = mock(SAAClient.class);
        resource = new RateResource(service, authorities);
    }

    @Test
    public void serviceTest() throws IOException {
        SAAResponse saaResponse = loadResponse(ResultType.OK, "/victoria-quay.json");
        when(service.search("EH66QQ")).thenReturn(saaResponse);

        Response response = resource.search("EH66QQ");

        assertThat(response.getStatus()).isEqualTo(200);
        SearchResponse search = (SearchResponse) response.getEntity();
        assertThat(search.getProperties()).hasSize(2);
        Property property = search.getProperties().get(0);
        assertThat(property.getOccupiers().get(0)).startsWith("SCOTTISH GOVERNMENT");
        assertThat(property.getCouncil()).isEqualTo("City of Edinburgh");
    }

    @Test
    public void serviceTestWithExtraValues() throws IOException {
        SAAResponse saaResponse = loadResponse(ResultType.OK, "/victoria-quay-extra-properties.json");
        when(service.search("EH66QQ")).thenReturn(saaResponse);

        Response response = resource.search("EH66QQ");

        SearchResponse search = (SearchResponse) response.getEntity();
        assertEquals(2, search.getProperties().size());
        assertThat(search.getProperties().get(0).getOccupiers().get(0)).startsWith("SCOTTISH GOVERNMENT");
    }

    @Test
    public void returnsNoResultsIfAPIReturns404() throws IOException {
        SAAResponse saaResponse = loadResponse(ResultType.NO_RESULTS, "/unknown.json");
        when(service.search("EH66QQ")).thenReturn(saaResponse);

        Response response = resource.search("EH66QQ");

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        SearchResponse search = (SearchResponse) response.getEntity();
        assertThat(search.getProperties()).isNullOrEmpty();
        assertThat(search.getResultType().equals(ResultType.NO_RESULTS));
    }

    @Test
    public void returnsTooManyResultsIfAPIReturns403() throws IOException {
        SAAResponse saaResponse = loadResponse(ResultType.TOO_MANY_RESULTS, "/too-many.json");
        when(service.search("Princes Street Edinburgh")).thenReturn(saaResponse);

        Response response = resource.search("Princes Street Edinburgh");

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        SearchResponse search = (SearchResponse) response.getEntity();
        assertThat(search.getProperties()).isNullOrEmpty();
        assertThat(search.getResultType().equals(ResultType.TOO_MANY_RESULTS));
    }

    private SAAResponse loadResponse(ResultType type, String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SAAResult result;
        try (InputStream is = getClass().getResourceAsStream(file)) {
            result = mapper.readValue(is, SAAResult.class);
        }
        return new SAAResponse(type, result);
    }

}
