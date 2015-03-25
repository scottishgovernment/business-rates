package org.mygovscot.services;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mygovscot.representations.LocalAuthority;
import org.mygovscot.representations.Postcode;
import org.mygovscot.representations.Property;
import org.mygovscot.representations.SearchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@DirtiesContext
public class RateServiceTest {

    @Test
    public void serviceTest() {
        Postcode postcode = new Postcode();
        postcode.setPostcode("EH6 6QQ");

        Property property =  new Property();
        property.setAddress("The address");
        property.setLocalAuthority(new LocalAuthority());

        Property propertyWithoutLA =  new Property();
        propertyWithoutLA.setAddress("The address without local authority");

        List<Property> properties = Arrays.asList(property, propertyWithoutLA);

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setProperties(properties);

        RestTemplate geoSearchTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(geoSearchTemplate.getForObject("geoUrl", SearchResponse.class, "G1 1PW")).thenReturn(searchResponse);

        RestTemplate saaTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(saaTemplate.getForEntity("geoUrl", Postcode.class, "G1 1PW")).thenReturn(new ResponseEntity<>(postcode, HttpStatus.OK));
        Mockito.when(saaTemplate.getForObject("saaUrl", SearchResponse.class, "EH66QQ")).thenReturn(searchResponse);

        RateService service = new RateService();
        ReflectionTestUtils.setField(service, "geoSearchTemplate", geoSearchTemplate);
        ReflectionTestUtils.setField(service, "geoUrl", "geoUrl");
        ReflectionTestUtils.setField(service, "saaTemplate", saaTemplate);
        ReflectionTestUtils.setField(service, "saaUrl", "saaUrl");

        SearchResponse search = service.search("EH66QQ");
        Assert.assertEquals(1, search.getProperties().size());
    }

    @Test( expected = HttpClientErrorException.class)
    public void service404LATest() {
        Postcode postcode = new Postcode();
        postcode.setPostcode("EH6 6QQ");

        // There is a property returned but it does not have an LA so it's rejected by the RateService.
        Property propertyWithoutLA =  new Property();
        propertyWithoutLA.setAddress("The address without local authority");

        List<Property> properties = Arrays.asList(propertyWithoutLA);

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setProperties(properties);

        RestTemplate geoSearchTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(geoSearchTemplate.getForObject("geoUrl", SearchResponse.class, "G1 1PW")).thenReturn(searchResponse);

        RestTemplate saaTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(saaTemplate.getForEntity("geoUrl", Postcode.class, "G1 1PW")).thenReturn(new ResponseEntity<>(postcode, HttpStatus.OK));
        Mockito.when(saaTemplate.getForObject("saaUrl", SearchResponse.class, "EH66QQ")).thenReturn(searchResponse);

        RateService service = new RateService();
        ReflectionTestUtils.setField(service, "geoSearchTemplate", geoSearchTemplate);
        ReflectionTestUtils.setField(service, "geoUrl", "geoUrl");
        ReflectionTestUtils.setField(service, "saaTemplate", saaTemplate);
        ReflectionTestUtils.setField(service, "saaUrl", "saaUrl");

        SearchResponse search = service.search("EH66QQ");
    }

    @Test
    public void getPostcodeTest() {
        RateService rateService = new RateService();

        String postcode = rateService.getPostcode("ONE\nTWO\nTHREE");
        Assert.assertEquals("THREE", postcode);
        postcode = rateService.getPostcode("ONE");
        Assert.assertEquals("ONE", postcode);
    }

    @Test
    public void getLocalAuthorityTest() {

        RestTemplate template = Mockito.mock(RestTemplate.class);
        Mockito.when(template.getForEntity("geoUrl", Postcode.class, "G1 1PW")).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.NOT_FOUND, ""));

        RateService service = new RateService();
        ReflectionTestUtils.setField(service, "geoSearchTemplate", template);
        ReflectionTestUtils.setField(service, "geoUrl", "geoUrl");

        service.getLocalAuthority("G1 1PW");

    }

}
