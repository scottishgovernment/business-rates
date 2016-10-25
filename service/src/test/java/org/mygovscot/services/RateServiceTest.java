package org.mygovscot.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mygovscot.representations.SearchResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@DirtiesContext
public class RateServiceTest {

    private RateService service;

    @Before
    public void setUp() throws IOException {
        LocalAuthorities authorities = new LocalAuthorities();
        authorities.load();
        service = new RateService(authorities);
    }

    @Test
    public void serviceTest() throws IOException {
        SearchResponse saaResponse = loadResponse("/victoria-quay.json");

        RestTemplate saaTemplate = Mockito.mock(RestTemplate.class);
        when(saaTemplate.getForObject(anyString(), eq(SearchResponse.class), eq("EH66QQ"), eq("saaKey"))).thenReturn(saaResponse);

        setField(service, "saaTemplate", saaTemplate);
        setField(service, "saaUrl", "saaUrl");
        setField(service, "saaKey", "saaKey");

        SearchResponse search = service.search("EH66QQ");
        assertEquals(2, search.getProperties().size());
        assertEquals("City of Edinburgh", search.getProperties().get(0).getCouncil());
    }

    @Test(expected = HttpClientErrorException.class)
    public void service404LATest() throws IOException {
        SearchResponse saaResponse = loadResponse("/unknown.json");

        RestTemplate saaTemplate = Mockito.mock(RestTemplate.class);
        when(saaTemplate.getForObject(anyString(), eq(SearchResponse.class), eq("EH66QQ"), eq("saaKey"))).thenReturn(saaResponse);

        setField(service, "saaTemplate", saaTemplate);
        setField(service, "saaUrl", "saaUrl");
        setField(service, "saaKey", "saaKey");

        service.search("EH66QQ");
    }

    private SearchResponse loadResponse(String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SearchResponse saaResponse;
        try (InputStream is = getClass().getResourceAsStream(file)) {
            saaResponse = mapper.readValue(is, SearchResponse.class);
        }
        return saaResponse;
    }

}
