package scot.mygov.business.rates.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import scot.mygov.business.rates.representations.Occupier;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SAAProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    private int ua;

    private String rv;

    private String address;

    private List<Occupier> occupier;

    public int getUa() {
        return ua;
    }

    public void setUa(int ua) {
        this.ua = ua;
    }

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
