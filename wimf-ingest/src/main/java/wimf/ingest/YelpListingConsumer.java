package wimf.ingest;

import io.reactivex.Observable;
import org.glassfish.jersey.client.ClientConfig;
import wimf.domain.Business;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

final class YelpListingConsumer {
    public static final String API_URL = "https://api.yelp.com/v3";
    public static final String PHONE_SEARCH_PATH = "/businesses/search/phone";

    private final String yelpToken;
    private final int maxYelpListings;
    private final Client client;

    YelpListingConsumer(final String yelpToken) {
        this(yelpToken, 0);
    }

    YelpListingConsumer(final String yelpToken,
                        final int maxYelpListings) {

        this.yelpToken = yelpToken;
        this.maxYelpListings = maxYelpListings;
        client = ClientBuilder.newClient(new ClientConfig());
    }

    Observable<Business> getListings(final Observable<RestaurantInspection> inspections) {
        return (maxYelpListings == 0 ? inspections : inspections.take(maxYelpListings))
                .map(Observable::just)
                .flatMap(o -> o.map(ri -> {
                    final YelpPhoneSearch ps = client
                            .target(API_URL)
                            .path(PHONE_SEARCH_PATH)
                            .queryParam("phone", "1" + ri.phone)
                            .request(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + yelpToken)
                            .get(YelpPhoneSearch.class);

                    return ps.businesses.size() < 1
                            ? Optional.empty()
                            : Optional.of(new Business(
                                    ps.businesses.get(0).rating,
                                    ps.businesses.get(0).price,
                                    ri.businessID));
                }), 5)
                .filter(Optional::isPresent)
                .map(o -> (Business)o.get());
    }
}
