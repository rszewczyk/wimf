package wimf.ingest;

import com.google.common.collect.Iterables;
import com.socrata.api.HttpLowLevel;
import com.socrata.api.Soda2Consumer;
import com.socrata.builders.SoqlQueryBuilder;
import com.socrata.exceptions.LongRunningQueryException;
import com.socrata.exceptions.SodaError;
import com.socrata.model.soql.OrderByClause;
import com.socrata.model.soql.SortOrder;
import io.reactivex.Observable;

import java.io.InputStream;
import java.util.Arrays;

/**
 * returns a stream of restaurant inspections from New York Open Data
 */
final class RestaurantInspectionConsumer {
    private final Soda2Consumer consumer;
    private final int pageSize;
    private final String resourceId;

    RestaurantInspectionConsumer(final int pageSize) {
        this(pageSize, "9w7m-hzhe", Soda2Consumer.newConsumer("https://data.cityofnewyork.us"));
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
                                        .addSelectPhrases(Arrays.asList("camis", "dba", "cuisine_description",
                                                "violation_code", "violation_description", "grade", "boro", "building",
                                                "street", "zipcode", "phone", "score", "inspection_date"))
                                        .setWhereClause("inspection_date > '2012-01-01T00:00:00' AND " +
                                                "boro IS NOT NULL AND " +
                                                "camis IS NOT NULL AND " +
                                                "grade IS NOT NULL" +
                                                "cuisine_description IS NOT NULL ")
                                        .setOrderByPhrase(Arrays.asList(
                                                new OrderByClause(SortOrder.Descending, "inspection_date")))
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
