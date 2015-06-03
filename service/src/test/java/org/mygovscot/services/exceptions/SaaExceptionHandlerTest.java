package org.mygovscot.services.exceptions;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import static org.junit.Assert.assertEquals;

public class SaaExceptionHandlerTest {

    @Test
    public void testHandleAddressNotFound() throws Exception {

        HttpClientErrorException e = new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        WebRequest request = Mockito.mock(WebRequest.class);

        SaaExceptionHandler handler = new SaaExceptionHandler();
        ResponseEntity<Object> entity = handler.handleAddressNotFound(e, request);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }
}