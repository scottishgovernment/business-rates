package scot.mygov.business.rates;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BusinessRatesConfiguration {

    private int port;

    private Health health = new Health();

    private SAA saa = new SAA();

    public int getPort() {
        return port;
    }

    public Health getHealth() {
        return health;
    }

    public SAA getSAA() {
        return saa;
    }

    public static class Health {

        private String search;

        @JsonProperty("cache_time")
        private String cacheTime = "PT1M";

        public void setSearch(String search) {
            this.search = search;
        }

        public String getSearch() {
            return search;
        }

        public String getCacheTime() {
            return cacheTime;
        }

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
