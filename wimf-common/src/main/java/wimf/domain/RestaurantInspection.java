package wimf.domain;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Model representing a New York Restaurant Inspection. There isn't a lot of
 * business logic here - just some validation for certain properties
 */
public class RestaurantInspection {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private String businessName;

    @NotEmptyString
    private String boro;

    private String grade;

    @NotNull
    private LocalDateTime inspectionDate;

    @NotEmptyString
    private String businessID;

    @NotEmptyString
    private String cuisine;

    private String violationCode;

    private int score;

    public String getBusinessName() {
        return businessName;
    }

    public String getBoro() {
        return boro;
    }

    public String getGrade() {
        return grade;
    }

    public LocalDateTime getInspectionDate() {
        return inspectionDate;
    }

    public String getBusinessID() {
        return businessID;
    }

    public String getCuisine() {
        return cuisine;
    }

    public String getViolationCode() {
        return violationCode;
    }

    public int getScore() {
        return score;
    }

    public RestaurantInspection(final String businessName,
                                final String boro,
                                final String grade,
                                final LocalDateTime inspectionDate,
                                final String businessID,
                                final String cuisine,
                                final String violationCode,
                                final int score) {
        this.businessName = businessName;
        this.boro = boro;
        this.grade = grade;
        this.inspectionDate = inspectionDate;
        this.businessID = businessID;
        this.cuisine = cuisine;
        this.violationCode = violationCode;
        this.score = score;
    }

    public static void save(final RestaurantInspectionDao dao, final RestaurantInspection inspection) {
        final Set<ConstraintViolation<RestaurantInspection>> cv = validator.validate(inspection);

        if (cv.size() > 0) {
            throw new ConstraintViolationException("invalid inspection", cv);
        }

        dao.insert(inspection);
    }

    public static List<RestaurantInspection> getPage(final RestaurantInspectionDao dao,
                                                     final PageParams params) {
        final Set<ConstraintViolation<PageParams>> cv = validator.validate(params);

        if (cv.size() > 0) {
            throw new ConstraintViolationException("invalid query parameters", cv);
        }

        return dao.fetchPage(params.limit, params.offset, params.sort, params.filter);
    }

    public static long count(final RestaurantInspectionDao dao) {
        return dao.count();
    }
}
