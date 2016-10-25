package org.mygovscot.representations;

import java.util.List;

public class SearchResponse {

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
