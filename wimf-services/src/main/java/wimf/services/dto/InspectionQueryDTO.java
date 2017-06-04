package wimf.services.dto;

import com.google.common.collect.ImmutableList;
import wimf.domain.PageParams;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Aggregates pagination and filter query params as a single {@link BeanParam}
 */
public final class InspectionQueryDTO {
    public final int limit;
    public final int offset;
    public final List<String> sort;
    public final List<String> filter;

    public PageParams toPageParams() {
        return new PageParams(limit, offset, sort, filter);
    }

    public InspectionQueryDTO(@DefaultValue("0") @QueryParam("limit") final int limit,
                              @DefaultValue("0") @QueryParam("offset") final int offset,
                              @QueryParam("sort") final List<String> sort,
                              @QueryParam("filter") final List<String> filter) {
        this.limit = limit;
        this.offset = offset;
        this.sort = ImmutableList.copyOf(sort);
        this.filter = ImmutableList.copyOf(filter);
    }
}
