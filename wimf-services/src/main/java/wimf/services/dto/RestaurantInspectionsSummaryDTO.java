package wimf.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import wimf.domain.RestaurantInspectionsSummary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class RestaurantInspectionsSummaryDTO {

    public static RestaurantInspectionsSummaryDTO fromModel(RestaurantInspectionsSummary summary) {
        ImmutableMap.Builder<String, List<TimestampAggregationDTO>> builder = ImmutableMap.builder();
        summary.gradesByDate.forEach((k, v) -> builder.put(k, TimestampAggregationDTO.fromModels(v)));

        return new RestaurantInspectionsSummaryDTO(summary.total, summary.gradeTotal, builder.build());
    }

    @JsonCreator
    public static RestaurantInspectionsSummaryDTO of(
            @JsonProperty("total") final long total,
            @JsonProperty("gradeTotal") final long gradeTotal,
            @JsonProperty("gradesByDate") final Map<String, List<TimestampAggregationDTO>> gradesByDate) {

        return new RestaurantInspectionsSummaryDTO(total, gradeTotal, ImmutableMap.copyOf(gradesByDate));
    }

    public final long total;
    public final long gradeTotal;
    public final Map<String, List<TimestampAggregationDTO>> gradesByDate;

    private RestaurantInspectionsSummaryDTO(final long total,
                                            final long gradeTotal,
                                            final Map<String, List<TimestampAggregationDTO>> gradesByDate) {
        this.total = total;
        this.gradeTotal = gradeTotal;
        this.gradesByDate = gradesByDate;
    }

    public static final class TimestampAggregationDTO {

        public static List<TimestampAggregationDTO> fromModels(
                List<RestaurantInspectionsSummary.Aggregation<LocalDateTime>> aggs) {

            return aggs.stream()
                    .map(a -> new TimestampAggregationDTO(a.value, a.count))
                    .collect(ImmutableList.toImmutableList());
        }

        @JsonCreator
        public static TimestampAggregationDTO of(@JsonProperty("value") final LocalDateTime value,
                                                 @JsonProperty("count") final long count) {
            return new TimestampAggregationDTO(value, count);
        }

        public final LocalDateTime value;
        public final long count;

        private TimestampAggregationDTO(final LocalDateTime value, final long count) {
            this.value = value;
            this.count = count;
        }
    }
}
