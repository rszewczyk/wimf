package wimf.domain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;

public final class RestaurantInspectionsSummary {

    private static final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    public static RestaurantInspectionsSummary get(final RestaurantInspectionDao dao,
                                                   final SummaryParams params) {
        final Set<ConstraintViolation<SummaryParams>> cv = validator.validate(params);

        if (cv.size() > 0) {
            throw new ConstraintViolationException(cv);
        }

        return new RestaurantInspectionsSummary(
                dao.count(params.filters),
                dao.countGrades(params.filters),
                dao.getMinDate(),
                dao.getMaxDate(),
                getGradesByDate(dao, params.filters),
                getGradesAgg(dao, params.filters, "boro"),
                getGradesAgg(dao, params.filters, "cuisine"),
                getGradesAgg(dao, params.filters, "inspection_type"),
                getTerms(dao, params.filters));
    }

    public final long total;
    public final long gradeTotal;
    public final LocalDateTime minDate;
    public final LocalDateTime maxDate;
    public final Map<String, List<Aggregation<LocalDateTime>>> gradesByDate;
    public final Map<String, List<Aggregation<String>>> gradesByBoro;
    public final Map<String, List<Aggregation<String>>> gradesByCuisine;
    public final Map<String, List<Aggregation<String>>> gradesByInspectionType;
    public final Map<String, List<Aggregation<String>>> terms;

    RestaurantInspectionsSummary(final long total,
                                 final long gradeTotal,
                                 final LocalDateTime minDate,
                                 final LocalDateTime maxDate,
                                 final Map<String, List<Aggregation<LocalDateTime>>> gradesByDate,
                                 final Map<String, List<Aggregation<String>>> gradesByBoro,
                                 final Map<String, List<Aggregation<String>>> gradesByCuisine,
                                 final Map<String, List<Aggregation<String>>> gradesByInspectionType,
                                 final Map<String, List<Aggregation<String>>> terms) {
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

    public static final class Aggregation<T> {
        public final T value;
        public final long count;

        Aggregation(final T value, final long count) {
            this.value = value;
            this.count = count;
        }
    }

    private static Map<String, List<Aggregation<String>>> getTerms(final RestaurantInspectionDao dao,
                                                                   final List<String> userFilter) {

        ImmutableMap.Builder<String, List<Aggregation<String>>> terms = ImmutableMap.builder();

        Arrays.asList("cuisine", "boro", "inspection_type").forEach(t -> terms.put(t, dao.countTerms(t, userFilter)));

        return terms.build();
    }

    private static Map<String, List<Aggregation<LocalDateTime>>> getGradesByDate(final RestaurantInspectionDao dao,
                                                                                 final List<String> userFilter) {

        ImmutableMap.Builder<String, List<Aggregation<LocalDateTime>>> gradesByDate = ImmutableMap.builder();

        Arrays.asList("A", "B", "C").forEach(g ->
                gradesByDate.put(
                        g,
                        dao.getGradeDateAggregation(
                                "inspection_date",
                                userFilter.stream()
                                        .filter(f -> !f.startsWith("inspection_date"))
                                        .collect(ImmutableList.toImmutableList()),
                                g))
        );

        return gradesByDate.build();
    }

    private static Map<String, List<Aggregation<String>>> getGradesAgg(final RestaurantInspectionDao dao,
                                                                       final List<String> userFilter,
                                                                       final String aggName) {

        ImmutableMap.Builder<String, List<Aggregation<String>>> grades = ImmutableMap.builder();

        Arrays.asList("A", "B", "C").forEach(g ->
                grades.put(g, dao.getGradeStringAggregation(aggName, userFilter, g))
        );

        return grades.build();
    }
}
