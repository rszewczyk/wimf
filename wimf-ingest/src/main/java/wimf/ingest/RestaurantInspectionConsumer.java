package wimf.ingest;

import com.google.common.collect.Iterables;
import com.socrata.api.HttpLowLevel;
import com.socrata.api.Soda2Consumer;
import com.socrata.builders.SoqlQueryBuilder;
import com.socrata.exceptions.LongRunningQueryException;
import com.socrata.exceptions.SodaError;
import io.reactivex.Observable;

import java.io.InputStream;

/**
 * returns a stream of restaurant inspections from New York Open Data
 */
final class RestaurantInspectionConsumer {
    private final Soda2Consumer consumer;
    private final int pageSize;
    private final String resourceId;

    public RestaurantInspectionConsumer() {
        consumer = Soda2Consumer.newConsumer("https://data.cityofnewyork.us");
        pageSize = 100_000;
        resourceId = "9w7m-hzhe";
    }

    RestaurantInspectionConsumer(final int pageSize,
                                        final String resourceId,
                                        final Soda2Consumer consumer) {
        this.consumer = consumer;
        this.pageSize = pageSize;
        this.resourceId = resourceId;
    }

    Observable<RestaurantInspection> getAll() throws SodaError, LongRunningQueryException {
        return getAll(0);
    }

    private Observable<RestaurantInspection> getAll(final int pageNumber) throws SodaError, LongRunningQueryException {
        return Observable.defer(() -> Observable.using(
                () -> consumer
                        .query(resourceId,
                                HttpLowLevel.JSON_TYPE,
                                new SoqlQueryBuilder()
                                        .addSelectPhrases(RestaurantInspection.DATA_FIELDS)
                                        .setOffset(pageNumber * pageSize)
                                        .setLimit(pageSize)
                                        .build())
                        .getEntityInputStream(),
                (final InputStream in) -> {
                    Iterable<RestaurantInspection> it = JsonIterable.from(in, RestaurantInspection.class);
                    if (Iterables.isEmpty(it)) {
                        return Observable.empty();
                    }

                    return Observable.fromIterable(it).concatWith(getAll(pageNumber + 1));
                },
                in -> in.close()
        ));
    }
}
