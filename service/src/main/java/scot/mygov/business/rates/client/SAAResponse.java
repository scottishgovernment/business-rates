package scot.mygov.business.rates.client;

public class SAAResponse {

    private ResultType type;

    private SAAResult result;

    public SAAResponse(ResultType type, SAAResult properties) {
        this.type = type;
        this.result = properties;
    }

    public ResultType getType() {
        return type;
    }

    public SAAResult getResult() {
        return result;
    }

}
