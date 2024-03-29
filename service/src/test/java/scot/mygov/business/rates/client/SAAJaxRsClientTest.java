package scot.mygov.business.rates.client;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import scot.mygov.business.rates.resources.LocalAuthorities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class SAAJaxRsClientTest {

    private SAAJaxRsClient service;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    private WebTarget webTarget;

    @Before
    public void setUp() throws IOException {
        UriBuilder uri = UriBuilder.fromUri("http://localhost/shared/webservices/vr.php")
                .port(wireMockRule.port())
                .queryParam("search", "{search}");
        LocalAuthorities authorities = new LocalAuthorities();
        authorities.load();
        this.webTarget = ClientBuilder.newClient().target(uri);
        this.service = new SAAJaxRsClient(webTarget);
    }

    @Test
    public void returnsPropertiesForSuccessfulSearch() throws IOException {
        stubFor(get(urlEqualTo("/shared/webservices/vr.php?search=EH66QQ"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBody(loadResponse("/victoria-quay.json"))));

        SAAResponse response = service.search("EH66QQ");

        assertThat(response.getType()).isEqualTo(ResultType.OK);
    }

    @Test
    public void returnsNoResultsFor404() throws IOException {
        stubFor(get(urlEqualTo("/shared/webservices/vr.php?search=PrincesX+Street+Edinburgh"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBody(loadResponse("/unknown.json"))));

        SAAResponse response = service.search("PrincesX Street Edinburgh");

        assertThat(response.getType()).isEqualTo(ResultType.NO_RESULTS);
    }

    @Test
    public void returnsTooManyResultsFor403() throws IOException {
        stubFor(get(urlEqualTo("/shared/webservices/vr.php?search=Princes+Street+Edinburgh"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(403)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBody(loadResponse("/unknown.json"))));

        SAAResponse response = service.search("Princes Street Edinburgh");

        assertThat(response.getType()).isEqualTo(ResultType.TOO_MANY_RESULTS);
    }

    private String loadResponse(String file) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(file)) {
            return IOUtils.toString(is, Charset.forName("UTF-8"));
        }
    }

}
