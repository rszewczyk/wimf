package wimf.ingest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
final class YelpPhoneSearch {

    @JsonCreator
    static YelpPhoneSearch of(@JsonProperty("businesses") final List<Business> businesses) {
        return new YelpPhoneSearch(businesses);
    }

    public final List<Business> businesses;

    private YelpPhoneSearch(final List<Business> businesses) {
        this.businesses = ImmutableList.copyOf(businesses);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Business {

        @JsonCreator
        static Business of(@JsonProperty("rating") final float rating,
                           @JsonProperty("price") final String price) {

            return new Business(rating, price);
        }

        public final float rating;
        public final String price;

        private Business(final float rating, final String price) {
            this.rating = rating;
            this.price = price;
        }
    }
}
