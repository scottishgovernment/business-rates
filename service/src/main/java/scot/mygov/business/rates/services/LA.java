package scot.mygov.business.rates.services;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LA {

    private final int id;

    private final String name;

    private final String link;

    @JsonCreator
    public LA(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("link") String link) {
        this.id = id;
        this.name = name;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

}
