package wimf.domain;

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
    abstract protected long count(final List<String> filter);
}
