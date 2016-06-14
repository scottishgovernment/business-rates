package org.mygovscot.config;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SAAClientRequestFactoryTest {

    private SAAClientHttpRequestFactory factory;

    private static final String LIVE_URL =
            "https://www.saa.gov.uk/shared/webservices/vr.php?search_term=";

    private static final String STAGE_URL =
            "https://staging.govusers.saa.gov.uk/shared/webservices/vr.php?search_term=";

    @Test
    public void connectionShouldAllowSelfSignedCertificateForStagingServer() throws IOException {
        factory = new SAAClientHttpRequestFactory(null, null);
        HttpsURLConnection connection = mock(HttpsURLConnection.class);
        when(connection.getURL()).thenReturn(URI.create(STAGE_URL).toURL());
        ArgumentCaptor<SSLSocketFactory> captor = ArgumentCaptor.forClass(SSLSocketFactory.class);

        factory.prepareConnection(connection, "GET");

        verify(connection).setSSLSocketFactory(captor.capture());
        assertEquals(factory.sslSocketFactory, captor.getValue());
    }


    @Test
    public void defaultSSLSocketFactoryShouldBeUsedForLiveServer() throws IOException {
        factory = new SAAClientHttpRequestFactory(null, null);
        HttpsURLConnection connection = mock(HttpsURLConnection.class);
        when(connection.getURL()).thenReturn(URI.create(LIVE_URL).toURL());

        factory.prepareConnection(connection, "GET");

        verify(connection, never()).setSSLSocketFactory(any());
    }

    @Test
    public void authorizationHeaderShouldBeSpecifiedIfCredentialsProvided() throws IOException {
        factory = new SAAClientHttpRequestFactory("username", "password1");
        HttpsURLConnection connection = mock(HttpsURLConnection.class);
        when(connection.getURL()).thenReturn(URI.create(LIVE_URL).toURL());
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        factory.prepareConnection(connection, "GET");

        verify(connection).setRequestProperty(eq("Authorization"), captor.capture());
        String value = captor.getValue();
        assertTrue("Basic auth must be used", value.startsWith("Basic "));
        byte[] bytes = DatatypeConverter.parseBase64Binary(value.substring(6));
        String credentials = new String(bytes, Charset.forName("ISO-8859-1"));
        assertEquals("username:password1", credentials);
    }

    @Test
    public void noAuthorizationHeaderShouldBeSpecifiedIfCredentialsProvided() throws IOException {
        factory = new SAAClientHttpRequestFactory("username", "password1");
        HttpsURLConnection connection = mock(HttpsURLConnection.class);
        when(connection.getURL()).thenReturn(URI.create(LIVE_URL).toURL());
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        factory.prepareConnection(connection, "GET");

        verify(connection).setRequestProperty(eq("Authorization"), captor.capture());
        String value = captor.getValue();
        assertTrue("Basic auth must be used", value.startsWith("Basic "));
        byte[] bytes = DatatypeConverter.parseBase64Binary(value.substring(6));
        String credentials = new String(bytes, Charset.forName("ISO-8859-1"));
        assertEquals("username:password1", credentials);
    }

}
