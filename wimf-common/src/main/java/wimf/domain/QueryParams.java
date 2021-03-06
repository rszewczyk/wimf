package wimf.domain;

import com.google.common.collect.ImmutableList;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * Holds and validates parameters for an inspection query
 */
final public class QueryParams {

    @Min(value=0)
    public final int limit;

    @Min(value=0)
    public final int offset;

    @ValidSort
    public final List<String> sort;

    @ValidFilter
    public final List<String> filter;

    public QueryParams(final int limit,
                       final int offset,
                       final List<String> sort,
                       final List<String> filter) {

        this.limit = limit;
        this.offset = offset;
        this.sort = ImmutableList.copyOf(sort);
        this.filter = ImmutableList.copyOf(filter);
    }
}
