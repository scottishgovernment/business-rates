package org.mygovscot.representations;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalAuthority implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private int code;

    private LocalAuthorityLinks links;

    public LocalAuthority() {
        // Default constructor
    }

    public LocalAuthority(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalAuthorityLinks getLinks() {
        return links;
    }

    public void setLinks(LocalAuthorityLinks links) {
        this.links = links;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
