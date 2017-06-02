package wimf.domain;

import java.util.List;

/**
 * closeable data access object for the {@link RestaurantInspection} model it's -
 * meant to combine with the JDBI attached interfaces so that consumers can have
 * control over the resource lifecycle without dealing directly with JDBI
 */
public interface RestaurantInspectionDao extends AutoCloseable {
    void insert(RestaurantInspection inspection);
    List<RestaurantInspection> fetchPage(int limit, int offset);
}
