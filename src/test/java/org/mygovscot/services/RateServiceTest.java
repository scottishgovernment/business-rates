package org.mygovscot.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mygovscot.Application;
import org.mygovscot.representations.SearchResponse;
import org.mygovscot.services.exceptions.AddressNotFoundException;
import org.mygovscot.services.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext
public class RateServiceTest {

    @Autowired
    RateService rateService;

    @Test
    public void serviceTest() {
        SearchResponse search = rateService.search("EH66QQ");
        Assert.assertEquals("Found", search.getMessage());
    }

    @Test(expected = TooManyResultsException.class)
    public void badServiceTest() {
        rateService.search("");
        Assert.fail("Should throw an exception because search is empty");
    }

    @Test(expected = AddressNotFoundException.class)
    public void badAddressTest() {
        rateService.search("THISADDRESSDOESNOTEXIST");
        Assert.fail("Should throw an exception because search is empty");
    }

    @Test
    public void getPostcodeTest() {
        String postcode = rateService.getPostcode("ONE\nTWO\nTHREE");
        Assert.assertEquals("THREE", postcode);
        postcode = rateService.getPostcode("ONE");
        Assert.assertEquals("ONE", postcode);
    }

}
