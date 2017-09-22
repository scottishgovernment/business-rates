package scot.mygov.business.rates.resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import scot.mygov.business.rates.representations.Property;
import scot.mygov.business.rates.representations.ResultType;
import scot.mygov.business.rates.representations.SearchResponse;
import scot.mygov.business.rates.services.LocalAuthorities;
import scot.mygov.business.rates.services.RateService;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RateResourceTest {

    private RateService service;

    private RateResource resource;

    @Before
    public void setUp() throws IOException {
        LocalAuthorities authorities = new LocalAuthorities();
        authorities.load();
        service = mock(RateService.class);
        resource = new RateResource(service);
    }

    @Test
    public void serviceTest() throws IOException {
        SearchResponse saaResponse = new SearchResponse();
        saaResponse.setProperties(new ArrayList<>(2));
        saaResponse.setResultType(ResultType.OK);
        when(service.search("EH66QQ")).thenReturn(saaResponse);

        Response response = resource.search("EH66QQ");

        assertThat(response.getStatus()).isEqualTo(200);
        SearchResponse search = (SearchResponse) response.getEntity();
        assertThat(search.getProperties()).hasSize(2);
        Property property = search.getProperties().get(0);
        assertThat(property.getOccupiers().get(0)).startsWith("SCOTTISH GOVERNMENT");
    }

    @Test
    public void serviceTestWithExtraValues() throws IOException {
        SearchResponse saaResponse = loadResponse("/victoria-quay-extra-properties.json", ResultType.OK);

        when(service.search("EH66QQ")).thenReturn(saaResponse);

        Response response = resource.search("EH66QQ");
        SearchResponse search = (SearchResponse) response.getEntity();
        assertEquals(2, search.getProperties().size());
        assertThat(search.getProperties().get(0).getOccupiers().get(0)).startsWith("SCOTTISH GOVERNMENT");
    }

    public void service404LATest() throws IOException {
        SearchResponse saaResponse = loadResponse("/unknown.json", ResultType.NO_RESULTS);

//        RestTemplate saaTemplate = Mockito.mock(RestTemplate.class);
//        when(saaTemplate.getForObject(anyString(), eq(SearchResponse.class), eq("EH66QQ"), eq("saaKey"))).thenReturn(saaResponse);
//
//        setField(service, "saaTemplate", saaTemplate);
//        setField(service, "saaUrl", "saaUrl");
//        setField(service, "saaKey", "saaKey");
//
//        resource.search("EH66QQ");
    }

    private SearchResponse loadResponse(String file, ResultType type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SearchResponse saaResponse;
        try (InputStream is = getClass().getResourceAsStream(file)) {
            saaResponse = mapper.readValue(is, SearchResponse.class);
        }
        saaResponse.setResultType(type);
        return saaResponse;
    }

}
