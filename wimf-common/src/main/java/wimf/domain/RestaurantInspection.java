package wimf.domain;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

/**
 * Model representing a New York Restaurant Inspection. There isn't a lot of
 * business logic here - just some validation for certain properties
 */
public class RestaurantInspection {

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

    public static void save(final RestaurantInspection inspection,
                     final RestaurantInspectionDao dao,
                     final Validator validator) {
        final Set<ConstraintViolation<RestaurantInspection>> cv = validator.validate(inspection);

        if (cv.size() > 0) {
            throw new ConstraintViolationException("validation failed", Collections.unmodifiableSet(cv));
        }

        dao.insert(inspection);
    }
}
