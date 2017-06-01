package wimf.domain;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Set;

public class RestaurantInspection {

    @NotEmptyString
    private String businessName;

    @NotEmptyString
    private String boro;

    public String getBusinessName() {
        return businessName;
    }

    public String getBoro() {
        return boro;
    }

    private RestaurantInspection(final String businessName,
                                 final String boro) {
        this.businessName = businessName;
        this.boro = boro;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String businessName;
        private String boro;

        public Builder businessName(final String businessName) {
            this.businessName = businessName;
            return this;
        }

        public Builder boro(final String boro) {
            this.boro = boro;
            return this;
        }

        public RestaurantInspection save(final RestaurantInspectionDAO dao, Validator validator) {
            final RestaurantInspection inspection =
                    new RestaurantInspection(this.businessName, this.boro);

            Set<ConstraintViolation<RestaurantInspection>> cv =
                    validator.validate(inspection);

            if (cv.size() > 0) {
                throw new ValidationException("validation failed");
            }

            dao.insert(this.businessName, this.boro);

            return inspection;
        }
    }
}
