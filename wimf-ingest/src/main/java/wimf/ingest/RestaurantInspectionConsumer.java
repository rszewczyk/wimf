package wimf.ingest;

import com.socrata.api.HttpLowLevel;
import com.socrata.api.Soda2Consumer;
import com.socrata.builders.SoqlQueryBuilder;
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

    Observable<RestaurantInspection> getAll() {
        return RxPageUtil.getAll(this::pager);
    }

    private IterableCloser<RestaurantInspection> pager(final int pageNumber) throws Exception {
        final InputStream in = consumer
                .query(resourceId,
                        HttpLowLevel.JSON_TYPE,
                        new SoqlQueryBuilder()
                                .addSelectPhrases(RestaurantInspection.DATA_FIELDS)
                                .setOffset(pageNumber * pageSize)
                                .setLimit(pageSize)
                                .build())
                .getEntityInputStream();

        return JsonIterable.from(in, RestaurantInspection.class);
    }
}
