package org.mygovscot.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mygovscot.representations.LocalAuthority;
import org.mygovscot.representations.Property;
import org.mygovscot.representations.SearchResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
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
        Property property =  new Property();
        property.setAddress("The address");

        Property propertyWithoutLA =  new Property();
        propertyWithoutLA.setAddress("Another address");

        List<Property> properties = asList(property, propertyWithoutLA);

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setProperties(properties);

        RestTemplate geoSearchTemplate = Mockito.mock(RestTemplate.class);
        when(geoSearchTemplate.getForObject("geoUrl", SearchResponse.class, "G1 1PW")).thenReturn(searchResponse);

        RestTemplate saaTemplate = Mockito.mock(RestTemplate.class);
        when(saaTemplate.getForObject(anyString(), eq(SearchResponse.class), eq("EH66QQ"), eq("saaKey"))).thenReturn(searchResponse);

        setField(service, "saaTemplate", saaTemplate);
        setField(service, "saaUrl", "saaUrl");
        setField(service, "saaKey", "saaKey");

        SearchResponse search = service.search("EH66QQ");
        assertEquals(2, search.getProperties().size());
    }

    @Test(expected = HttpClientErrorException.class)
    public void service404LATest() {
        List<Property> properties = emptyList();

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setProperties(properties);

        RestTemplate saaTemplate = Mockito.mock(RestTemplate.class);
        when(saaTemplate.getForObject(anyString(), eq(SearchResponse.class), eq("EH66QQ"), eq("saaKey"))).thenReturn(searchResponse);

        setField(service, "saaTemplate", saaTemplate);
        setField(service, "saaUrl", "saaUrl");
        setField(service, "saaKey", "saaKey");

        service.search("EH66QQ");
    }

}
