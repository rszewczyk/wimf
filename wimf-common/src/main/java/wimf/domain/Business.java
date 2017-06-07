package wimf.domain;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

public class Business {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @NotEmptyString
    public final String businessId;

    public final float rating;

    public final String price;

    public Business(final float rating, final String price, final String businessId) {
        this.rating = rating;
        this.price = price;
        this.businessId = businessId;
    }

    public static void save(final RestaurantInspectionDao dao, final List<Business> businesses) {
        businesses.forEach(Business::validate);
        dao.insertBusinesses(businesses);
    }

    private void validate() {
        final Set<ConstraintViolation<Business>> cv = validator.validate(this);

        if (cv.size() > 0) {
            throw new ConstraintViolationException(cv);
        }
    }

    // these are required for JDBI @BindBean

    public float getRating() {
        return rating;
    }

    public String getPrice() {
        return price;
    }

    public String getBusinessId() {
        return businessId;
    }
}
