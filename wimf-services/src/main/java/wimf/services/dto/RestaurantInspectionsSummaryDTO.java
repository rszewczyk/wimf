package wimf.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import wimf.domain.RestaurantInspectionsSummary;

public final class RestaurantInspectionsSummaryDTO {

    public static RestaurantInspectionsSummaryDTO fromModel(RestaurantInspectionsSummary summary) {
        return new RestaurantInspectionsSummaryDTO(summary.total);
    }

    @JsonCreator
    public static RestaurantInspectionsSummaryDTO of(@JsonProperty("total") final long total) {
        return new RestaurantInspectionsSummaryDTO(total);
    }

    public final long total;

    public RestaurantInspectionsSummaryDTO(final long total) {
        this.total = total;
    }
}
