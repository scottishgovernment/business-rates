package org.mygovscot.representations;

public class Occupier {

    private String name;

    public Occupier() {
        // Default constructor
    }

    public Occupier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
