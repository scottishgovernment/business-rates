package scot.mygov.business.rates.resources;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Provides access to web links for local authorities.
 * Links provided include the home page and the business rates page, if applicable.
 */
public class LocalAuthorities {

    public static final String AUTHORITIES_YAML = "/local-authorities.yaml";

    final Map<Integer, LA> authorities = new LinkedHashMap<>();

    public LA getAuthority(int id) {
        return authorities.get(id);
    }

    @PostConstruct
    public void load() throws IOException {
        InputStream is = getClass().getResourceAsStream(AUTHORITIES_YAML);
        LA[] las;
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            JsonParser parser = mapper.getFactory().createParser(is);
            las = parser.readValueAs(LA[].class);
        } catch (IOException ex) {
            IOUtils.closeQuietly(is);
            throw ex;
        }
        Map<Integer, LA> map = stream(las).collect(toMap(
                LA::getId,
                identity(),
                throwingMerger(),
                LinkedHashMap::new));
        authorities.putAll(map);
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }

}
