package wimf.domain;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Holds and validates parameters for an inspection summary
 */
public final class SummaryParams {
    @ValidFilter
    public final List<String> filters;

    public SummaryParams(final List<String> filters) {
        this.filters = ImmutableList.copyOf(filters);
    }
}
