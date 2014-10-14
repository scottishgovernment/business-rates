package org.mygovscot.services;

import org.mygovscot.representations.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/address")
public class RateService {

    @Autowired
    private RestTemplate template;

    @Value("${saa.url}")
    private String saaUrl;

    @RequestMapping(method = RequestMethod.GET)
    @Cacheable("saa.search")
    public SearchResponse search(@RequestParam(value = "search", required = true) String search) {

        return template.getForObject(saaUrl, SearchResponse.class, search);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unable to process the request.")
    @ExceptionHandler(HttpClientErrorException.class)
    public void forbidden() {
        // Nothing to do
    }

}
