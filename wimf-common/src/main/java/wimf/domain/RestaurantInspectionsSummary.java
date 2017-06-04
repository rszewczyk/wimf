package wimf.domain;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public final class RestaurantInspectionsSummary {

    private static final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    public static RestaurantInspectionsSummary get(final RestaurantInspectionDao dao,
                                                   final SummaryParams params) {
        final Set<ConstraintViolation<SummaryParams>> cv = validator.validate(params);

        if (cv.size() > 0) {
            throw new ConstraintViolationException(cv);
        }
        final long count = dao.count(params.filters);

        return new RestaurantInspectionsSummary(count);
    }

    public final long total;

    public RestaurantInspectionsSummary(final long total) {
        this.total = total;
    }
}
