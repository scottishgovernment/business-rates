package org.mygovscot.services.exceptions;

public class AddressNotFoundException extends RuntimeException {
    
    private String search;

    public AddressNotFoundException(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }
}
