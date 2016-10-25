package org.mygovscot.representations;

import java.io.Serializable;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Property implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rv;

    private String address;

    private int ua;

    private List<Occupier> occupier;

    private LocalAuthority localAuthority;

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

    public LocalAuthority getLocalAuthority() {
        return localAuthority;
    }

    public void setLocalAuthority(LocalAuthority localAuthority) {
        this.localAuthority = localAuthority;
    }

    public int getUa() {
        return ua;
    }

    public void setUa(int ua) {
        this.ua = ua;
    }

    public String getValue() {
        return rv;
    }

    public String getCouncil() {
        return localAuthority.getName();
    }

    public String getLink() {
        return localAuthority.getLinks().getTax();
    }

    public List<String> getOccupiers() {
        return occupier.stream().map(Occupier::getName).collect(toList());
    }

}
