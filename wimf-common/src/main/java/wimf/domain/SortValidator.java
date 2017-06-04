package wimf.domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class SortValidator implements ConstraintValidator<ValidSort, List<String>> {

    @Override
    public void initialize(final ValidSort constraintAnnotation) {
        // nop
    }

    @Override
    public boolean isValid(final List<String> value, final ConstraintValidatorContext context) {
        for(int i = 0; i < value.size(); i++) {
            final String[] tokens = value.get(i).split(RestaurantInspectionUtil.SORT_SPLITTER);
            if (tokens.length != 2) {
                context.buildConstraintViolationWithTemplate(
                        "Malformed sort parameter `" + value.get(i) + "` at position `" + i +
                                "`. Must of the form: `sort_field sort_order` where sort_order is one " +
                                "of [ASC, DESC].")
                        .addConstraintViolation();
                return false;
            }
            if (!(tokens[1].equals(RestaurantInspectionUtil.SORT_ASC) ||
                    tokens[1].equals(RestaurantInspectionUtil.SORT_DESC))) {
                context.buildConstraintViolationWithTemplate(
                        "Invalid sort order `" + tokens[1] +
                                "` at position `" + i + "`. Must be one of [" +
                                RestaurantInspectionUtil.SORT_ASC + ", " +
                                RestaurantInspectionUtil.SORT_DESC + "].")
                        .addConstraintViolation();
                return false;
            }
            if (!RestaurantInspectionUtil.VALID_FIELDS.contains(tokens[0])) {
                context.buildConstraintViolationWithTemplate("Unknown sort field `" +
                        tokens[0] + "` at position `" + i + "`. Must be one of [" +
                        String.join(", ", RestaurantInspectionUtil.VALID_FIELDS) + "].")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
