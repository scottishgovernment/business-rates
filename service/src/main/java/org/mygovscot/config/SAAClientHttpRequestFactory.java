package org.mygovscot.config;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Creates {@link org.springframework.http.client.ClientHttpRequest} instances
 * for querying the SAA business rates web service.
 */
class SAAClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    static SSLSocketFactory sslSocketFactory = sslSocketFactoryForStaging();

    private final String username;

    private final String password;

    SAAClientHttpRequestFactory(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection) {
            setCredentials((HttpsURLConnection) connection);
        }
        super.prepareConnection(connection, httpMethod);
    }

    private void setCredentials(HttpsURLConnection connection) {
        if (connection.getURL().getHost().contains("staging")) {
            connection.setSSLSocketFactory(sslSocketFactory);
        }
        boolean usingCredentials = username != null && password != null;
        if (usingCredentials) {
            Charset charset = Charset.forName("ISO-8859-1");
            byte[] bytes = (username + ':' + password).getBytes(charset);
            String base64 = DatatypeConverter.printBase64Binary(bytes);
            connection.setRequestProperty("Authorization", "Basic " + base64);
        }
    }


    private static SSLSocketFactory sslSocketFactoryForStaging() {
        try {
            InputStream is = ProxyConfiguration.class.getResourceAsStream("/staging.crt");
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(is);

            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());

            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null);
            ks.setCertificateEntry("saa-staging", cert);
            tmf.init(ks);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            return sslContext.getSocketFactory();

        } catch (GeneralSecurityException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
