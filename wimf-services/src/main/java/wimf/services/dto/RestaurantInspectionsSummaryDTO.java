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

    public static RestaurantInspectionsSummaryDTO fromModel(final RestaurantInspectionsSummary summary) {
        final ImmutableMap.Builder<String, List<TimestampAggregationDTO>> builder = ImmutableMap.builder();
        summary.gradesByDate.forEach((k, v) -> builder.put(k, TimestampAggregationDTO.fromModels(v)));

        final ImmutableMap.Builder<String, List<StringAggregationDTO>> boroBuilder = ImmutableMap.builder();
        summary.gradesByBoro.forEach((k, v) -> boroBuilder.put(k, StringAggregationDTO.fromModels(v)));

        final ImmutableMap.Builder<String, List<StringAggregationDTO>> cuisineBuilder = ImmutableMap.builder();
        summary.gradesByCuisine.forEach((k, v) -> cuisineBuilder.put(k, StringAggregationDTO.fromModels(v)));

        final ImmutableMap.Builder<String, List<StringAggregationDTO>> inspectionTypeBuilder = ImmutableMap.builder();
        summary.gradesByInspectionType.forEach((k, v) -> inspectionTypeBuilder.put(k, StringAggregationDTO.fromModels(v)));

        final ImmutableMap.Builder<String, List<String>> termsBuilder = ImmutableMap.builder();
        summary.terms.forEach(termsBuilder::put);

        return new RestaurantInspectionsSummaryDTO(
                summary.total,
                summary.gradeTotal,
                summary.minDate,
                summary.maxDate,
                builder.build(),
                boroBuilder.build(),
                cuisineBuilder.build(),
                inspectionTypeBuilder.build(),
                termsBuilder.build());
    }

    @JsonCreator
    public static RestaurantInspectionsSummaryDTO of(
            @JsonProperty("total") final long total,
            @JsonProperty("gradeTotal") final long gradeTotal,
            @JsonProperty("minDate") final LocalDateTime minDate,
            @JsonProperty("maxDate") final LocalDateTime maxDate,
            @JsonProperty("gradesByDate") final Map<String, List<TimestampAggregationDTO>> gradesByDate,
            @JsonProperty("gradesByBoro") final Map<String, List<StringAggregationDTO>> gradesByBoro,
            @JsonProperty("gradesByCuisine") final Map<String, List<StringAggregationDTO>> gradesByCuisine,
            @JsonProperty("gradesByInspectionType") final Map<String, List<StringAggregationDTO>> gradesByInspectionType,
            @JsonProperty("terms") final Map<String, List<String>> terms) {

        return new RestaurantInspectionsSummaryDTO(
                total,
                gradeTotal,
                minDate,
                maxDate,
                ImmutableMap.copyOf(gradesByDate),
                ImmutableMap.copyOf(gradesByBoro),
                ImmutableMap.copyOf(gradesByCuisine),
                ImmutableMap.copyOf(gradesByInspectionType),
                ImmutableMap.copyOf(terms));
    }

    public final long total;
    public final long gradeTotal;
    public final LocalDateTime minDate;
    public final LocalDateTime maxDate;
    public final Map<String, List<TimestampAggregationDTO>> gradesByDate;
    public final Map<String, List<StringAggregationDTO>> gradesByBoro;
    public final Map<String, List<StringAggregationDTO>> gradesByCuisine;
    public final Map<String, List<StringAggregationDTO>> gradesByInspectionType;
    public final Map<String, List<String>> terms;

    private RestaurantInspectionsSummaryDTO(final long total,
                                            final long gradeTotal,
                                            final LocalDateTime minDate,
                                            final LocalDateTime maxDate,
                                            final Map<String, List<TimestampAggregationDTO>> gradesByDate,
                                            final Map<String, List<StringAggregationDTO>> gradesByBoro,
                                            final Map<String, List<StringAggregationDTO>> gradesByCuisine,
                                            final Map<String, List<StringAggregationDTO>> gradesByInspectionType,
                                            final Map<String, List<String>> terms) {
        this.total = total;
        this.gradeTotal = gradeTotal;
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.gradesByDate = gradesByDate;
        this.gradesByBoro = gradesByBoro;
        this.gradesByCuisine = gradesByCuisine;
        this.gradesByInspectionType = gradesByInspectionType;
        this.terms = terms;
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

    public static final class StringAggregationDTO {

        public static List<StringAggregationDTO> fromModels(
                List<RestaurantInspectionsSummary.Aggregation<String>> aggs) {

            return aggs.stream()
                    .map(a -> new StringAggregationDTO(a.value, a.count))
                    .collect(ImmutableList.toImmutableList());
        }

        @JsonCreator
        public static StringAggregationDTO of(@JsonProperty("value") final String value,
                                              @JsonProperty("count") final long count) {

            return new StringAggregationDTO(value, count);
        }

        public final String value;
        public final long count;

        private StringAggregationDTO(final String value, final long count) {
            this.value = value;
            this.count = count;
        }
    }
}
