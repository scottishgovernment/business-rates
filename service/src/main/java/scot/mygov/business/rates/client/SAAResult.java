package scot.mygov.business.rates.client;

import java.util.List;

public class SAAResult {

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
