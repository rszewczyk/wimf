package wimf.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class ResultSetDTO {
    public final List<RestaurantInspectionDTO> items;
    public final long total;

    static ResultSetDTO from(final long total, final List<RestaurantInspectionDTO> items) {
        return new ResultSetDTO(total, items);
    }

    @JsonCreator
    public static ResultSetDTO of(@JsonProperty("total") final long total,
                                  @JsonProperty("items") final List<RestaurantInspectionDTO> items) {
        return new ResultSetDTO(total, items);
    }

    private ResultSetDTO(final long total, final List<RestaurantInspectionDTO> items) {
        this.total = total;
        this.items = items;
    }
}
