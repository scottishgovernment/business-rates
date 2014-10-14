package org.mygovscot.representations;

import java.io.Serializable;
import java.util.List;

public class Property implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rv;

    private String address;

    private List<Occupier> occupier;

    public String getRv() {
        return rv;
    }

    public void setRv(String rv) {
        this.rv = rv;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Occupier> getOccupier() {
        return occupier;
    }

    public void setOccupier(List<Occupier> occupier) {
        this.occupier = occupier;
    }
}
