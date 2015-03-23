package org.mygovscot.services.exceptions;

public class TooManyResultsException extends RuntimeException {

    private String search;

    public TooManyResultsException(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }
}
