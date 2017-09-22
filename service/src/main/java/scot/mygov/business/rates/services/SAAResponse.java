package scot.mygov.business.rates.services;

import java.util.List;

/**
 * Serializable to allow caching of responses.
 */
public class SAAResponse {

    private static final long serialVersionUID = 1L;

    private String message;

    private List<SAAProperty> properties;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SAAProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SAAProperty> properties) {
        this.properties = properties;
    }

}
