package org.mygovscot.services;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.io.IOUtils;
import org.mygovscot.representations.LocalAuthority;
import org.mygovscot.representations.LocalAuthorityLinks;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides access to web links for local authorities.
 * Links provided include the home page and the business rates page, if applicable.
 */
@Repository
public class LocalAuthorities {

    public static final String AUTHORITIES_JSON = "/local-authorities.json";

    private final Map<String, LocalAuthority> authorities = new HashMap<>();

    public LocalAuthority getAuthority(String id) {
        return authorities.get(id);
    }

    @PostConstruct
    public void load() throws IOException {
        InputStream is = getClass().getResourceAsStream(AUTHORITIES_JSON);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonParser parser = mapper.getFactory().createParser(is);
            ArrayNode array = parser.readValueAsTree();
            for (JsonNode node : array) {
                LocalAuthority authority = convert(node);
                authorities.put(authority.getId(), authority);
            }
        } catch (IOException ex) {
            IOUtils.closeQuietly(is);
            throw ex;
        }
    }

    private static LocalAuthority convert(JsonNode node) {
        LocalAuthority authority = new LocalAuthority();
        authority.setId(node.get("id").asText());
        authority.setName(node.get("name").asText());
        LocalAuthorityLinks links = new LocalAuthorityLinks();
        authority.setLinks(links);
        links.setHomepage(node.get("homepage").asText(null));
        links.setTax(node.get("tax").asText(null));
        return authority;
    }

}
