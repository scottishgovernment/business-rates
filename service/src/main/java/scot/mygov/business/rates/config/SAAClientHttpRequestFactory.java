package scot.mygov.business.rates.config;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

/**
 * Creates {@link org.springframework.http.client.ClientHttpRequest} instances
 * for querying the SAA business rates web service.
 */
class SAAClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

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
        boolean usingCredentials = username != null && password != null;
        if (usingCredentials) {
            Charset charset = Charset.forName("ISO-8859-1");
            byte[] bytes = (username + ':' + password).getBytes(charset);
            String base64 = DatatypeConverter.printBase64Binary(bytes);
            connection.setRequestProperty("Authorization", "Basic " + base64);
        }
    }

}
