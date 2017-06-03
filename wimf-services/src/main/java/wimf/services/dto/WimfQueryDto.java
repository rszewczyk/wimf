package wimf.services.dto;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

/**
 * Aggregates pagination and filter query params as a single {@link BeanParam}
 */
public class WimfQueryDto {
    public final int limit;
    public final int offset;

    public WimfQueryDto(@DefaultValue("0") @QueryParam("limit") final int limit,
                        @DefaultValue("0") @QueryParam("offset") final int offset) {
        this.limit = limit;
        this.offset = offset;
    }
}
