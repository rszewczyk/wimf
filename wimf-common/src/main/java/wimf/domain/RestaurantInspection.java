package wimf.domain;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

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

    private RestaurantInspection(final String businessName,
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

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String businessName;
        private String boro;
        private String grade;
        private LocalDateTime inspectionDate;
        private String businessID;
        private String cuisine;
        private String violationCode;
        private int score;

        public Builder businessName(final String businessName) {
            this.businessName = businessName;
            return this;
        }

        public Builder boro(final String boro) {
            this.boro = boro;
            return this;
        }

        public Builder grade(final String grade) {
            this.grade = grade;
            return this;
        }

        public Builder inspectionDate(final LocalDateTime inspectionDate) {
            this.inspectionDate = inspectionDate;
            return this;
        }

        public Builder businessID(final String businessID) {
            this.businessID = businessID;
            return this;
        }

        public Builder cuisine(final String cuisine) {
            this.cuisine = cuisine;
            return this;
        }

        public Builder violationCode(final String violationCode) {
            this.violationCode = violationCode;
            return this;
        }

        public Builder score(final int score) {
            this.score = score;
            return this;
        }

        public RestaurantInspection save(final RestaurantInspectionDAO dao, final Validator validator) {
            final RestaurantInspection inspection =
                    new RestaurantInspection(this.businessName, this.boro, this.grade, this.inspectionDate,
                            this.businessID, this.cuisine, this.violationCode, this.score);

            final Set<ConstraintViolation<RestaurantInspection>> cv = validator.validate(inspection);

            if (cv.size() > 0) {
                throw ValidationException.from(cv);
            }

            dao.insert(inspection);

            return inspection;
        }
    }
}
