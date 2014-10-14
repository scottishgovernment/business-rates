package org.mygovscot.representations;

import java.io.Serializable;

public class Occupier implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

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
