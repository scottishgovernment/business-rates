package org.mygovscot.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mygovscot.Application;
import org.mygovscot.representations.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Test
    public void getPostcodeTest() {
        String postcode = rateService.getPostcode("ONE\nTWO\nTHREE");
        Assert.assertEquals("THREE", postcode);
        postcode = rateService.getPostcode("ONE");
        Assert.assertEquals("ONE", postcode);
    }

}
