package wimf.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * closeable data access object for the {@link RestaurantInspection} model it's -
 * meant to combine with the JDBI attached interfaces so that consumers can have
 * control over the resource lifecycle without dealing directly with JDBI
 */
public abstract class RestaurantInspectionDao implements AutoCloseable {
    abstract protected void insert(RestaurantInspection inspection);

    abstract protected List<RestaurantInspection> fetchPage(final int limit,
                                                            final int offset,
                                                            final List<String> sort);

    abstract protected List<RestaurantInspection> fetchPage(final int limit,
                                                            final int offset,
                                                            final List<String> sort,
                                                            final List<String> filter);

    abstract protected List<RestaurantInspectionsSummary.Aggregation<String>> getGradeStringAggregation(final String aggName,
                                                                                                        final List<String> userFilter,
                                                                                                        final String grade);

    abstract protected List<RestaurantInspectionsSummary.Aggregation<LocalDateTime>> getGradeDateAggregation(final String aggName,
                                                                                                             final List<String> userFilter,
                                                                                                             final String grade);

    abstract protected long count(final List<String> filter);

    abstract protected long countGrades(final List<String> filter);

    abstract protected List<String> groupTerms(final String field);

    abstract protected LocalDateTime getMinDate();

    abstract protected LocalDateTime getMaxDate();
}
