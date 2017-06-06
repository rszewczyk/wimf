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

    public final String businessName;

    @NotEmptyString
    public final String boro;

    @NotEmptyString
    public final String grade;

    @NotNull
    public final LocalDateTime inspectionDate;

    @NotEmptyString
    public final String businessID;

    @NotEmptyString
    public final String cuisine;

    @NotEmptyString
    public final String violationCode;

    public final String violationDescription;

    public final String inspectionType;

    public final int score;

    public RestaurantInspection(final String businessName,
                                final String boro,
                                final String grade,
                                final LocalDateTime inspectionDate,
                                final String businessID,
                                final String cuisine,
                                final String violationCode,
                                final String violationDescription,
                                final int score,
                                final String inspectionType) {

        this.businessName = businessName;
        this.boro = boro;
        this.grade = grade;
        this.inspectionDate = LocalDateTime.of(
                inspectionDate.getYear(),
                inspectionDate.getMonth(),
                inspectionDate.getDayOfMonth(),
                0,
                0);
        this.businessID = businessID;
        this.cuisine = cuisine;
        this.violationCode = violationCode;
        this.violationDescription = violationDescription;
        this.score = score;
        this.inspectionType = inspectionType;
    }

    public static void save(final RestaurantInspectionDao dao, final RestaurantInspection inspection) {
        final Set<ConstraintViolation<RestaurantInspection>> cv = validator.validate(inspection);

        if (cv.size() > 0) {
            throw new ConstraintViolationException(cv);
        }

        dao.insert(inspection);
    }

    public static List<RestaurantInspection> query(final RestaurantInspectionDao dao,
                                                   final QueryParams params) {
        final Set<ConstraintViolation<QueryParams>> cv = validator.validate(params);

        if (cv.size() > 0) {
            throw new ConstraintViolationException(cv);
        }

        return dao.fetchPage(params.limit, params.offset, params.sort, params.filter);
    }

    // JDBI BindBean needs getters
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

    public String getInspectionType() {
        return inspectionType;
    }

    public String getViolationDescription() { return violationDescription; }

    public int getScore() {
        return score;
    }
}
