package scot.mygov.business.rates.client;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The type of result returned by the SAA API.
 */
public enum ResultType {

    OK(200, "ok"),
    NO_RESULTS(404, "no-results"),
    TOO_MANY_RESULTS(403, "too-many-results");

    private final int statusCode;

    private final String type;

    ResultType(int statusCode, String type) {
        this.statusCode = statusCode;
        this.type = type;
    }

    public static ResultType of(int statusCode) {
        for (ResultType resultType : ResultType.values()) {
            if (resultType.statusCode == statusCode) {
                return resultType;
            }
        }
        throw new RuntimeException("Unexpected status code " + statusCode);
    }

    @JsonValue
    public String getType() {
        return type;
    }

}
