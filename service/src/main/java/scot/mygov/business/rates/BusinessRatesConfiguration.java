package scot.mygov.business.rates;

public class BusinessRatesConfiguration {

    private int port;

    private SAA saa = new SAA();

    public int getPort() {
        return port;
    }

    public SAA getSAA() {
        return saa;
    }

    public static class SAA {

        private String url;

        private String key;

        public String getUrl() {
            return url;
        }

        public String getKey() {
            return key;
        }

    }

}
