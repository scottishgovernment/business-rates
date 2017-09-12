package scot.mygov.business.rates.representations;

import java.io.Serializable;
import java.util.List;

/**
 * Serializable to allow caching of responses.
 */
public class SearchResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;

    private List<Property> properties;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
}
