package wimf.domain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RestaurantInspectionsSummary {

    private static final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    public static RestaurantInspectionsSummary get(final RestaurantInspectionDao dao,
                                                   final SummaryParams params) {
        final Set<ConstraintViolation<SummaryParams>> cv = validator.validate(params);

        if (cv.size() > 0) {
            throw new ConstraintViolationException(cv);
        }

        final long total = dao.count(params.filters);
        final long gradeTotal = dao.countGrades(params.filters);

        return new RestaurantInspectionsSummary(
                total,
                gradeTotal,
                getGradesByDate(dao, params.filters),
                getGradesAgg(dao, params.filters, "boro"),
                getGradesAgg(dao, params.filters, "cuisine"));
    }

    public final long total;
    public final long gradeTotal;
    public final Map<String, List<Aggregation<LocalDateTime>>> gradesByDate;
    public final Map<String, List<Aggregation<String>>> gradesByBoro;
    public final Map<String, List<Aggregation<String>>> gradesByCuisine;

    RestaurantInspectionsSummary(final long total,
                                 final long gradeTotal,
                                 final Map<String, List<Aggregation<LocalDateTime>>> gradesByDate,
                                 final Map<String, List<Aggregation<String>>> gradesByBoro,
                                 final Map<String, List<Aggregation<String>>> gradesByCuisine) {
        this.total = total;
        this.gradeTotal = gradeTotal;
        this.gradesByDate = gradesByDate;
        this.gradesByBoro = gradesByBoro;
        this.gradesByCuisine = gradesByCuisine;
    }

    public static final class Aggregation<T> {
        public final T value;
        public final long count;

        Aggregation(final T value, final long count) {
            this.value = value;
            this.count = count;
        }
    }

    private static Map<String, List<Aggregation<LocalDateTime>>> getGradesByDate(final RestaurantInspectionDao dao,
                                                                                 final List<String> userFilter) {

        ImmutableMap.Builder<String, List<Aggregation<LocalDateTime>>> gradesByDate = ImmutableMap.builder();

        Arrays.asList("A", "B", "C").forEach(g ->
            gradesByDate.put(g, dao.getGradeDateAggregation(
                    "inspection_date",
                    Stream.concat(
                            Stream.of("grade=" + g),
                            userFilter.stream())
                        .collect(ImmutableList.toImmutableList())))
        );

        return gradesByDate.build();
    }

    private static Map<String, List<Aggregation<String>>> getGradesAgg(final RestaurantInspectionDao dao,
                                                                       final List<String> userFilter,
                                                                       final String aggName) {

        ImmutableMap.Builder<String, List<Aggregation<String>>> grades = ImmutableMap.builder();

        Arrays.asList("A", "B", "C").forEach(g ->
                grades.put(g, dao.getGradeStringAggregation(
                        aggName,
                        Stream.concat(
                                Stream.of("grade=" + g),
                                userFilter.stream())
                                .collect(ImmutableList.toImmutableList())))
        );

        return grades.build();
    }
}
