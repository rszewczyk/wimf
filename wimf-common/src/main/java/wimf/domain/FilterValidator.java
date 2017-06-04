package wimf.domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.List;

public class FilterValidator implements ConstraintValidator<ValidFilter, List<String>> {
    @Override
    public void initialize(final ValidFilter constraintAnnotation) {
        // nop
    }

    @Override
    public boolean isValid(final List<String> value, final ConstraintValidatorContext context) {
        for (int i = 0; i < value.size(); i++) {
            final String[] tokens = value.get(i).split(RestaurantInspectionUtil.FILTER_SPLITTER);
            if (tokens.length != 3) {
                context.buildConstraintViolationWithTemplate(
                        "Malformed filter `" + value.get(i) + "` at position `" + i +
                                "`. Filter parameters must be of the form `filter_field op value` " +
                                "where op is one of [<, >, =, !].")
                        .addConstraintViolation();
                return false;
            }
            if (!RestaurantInspectionUtil.VALID_FIELDS.contains(tokens[0])) {
                context.buildConstraintViolationWithTemplate(
                        "Unknown filter field `" + tokens[0] + "` at position `" + i + "`. Must be one of [" +
                                String.join(", ", RestaurantInspectionUtil.VALID_FIELDS) + "].")
                        .addConstraintViolation();
                return false;
            }
            if (tokens[0].equals("score")) {
                try {
                    final int score = Integer.parseInt(tokens[2]);
                    if (score < 0) {
                        context.buildConstraintViolationWithTemplate(
                                "Invalid filter value `" + tokens[2] + "` at position `" + i +
                                        "`. Score must be a positive integer.")
                                .addConstraintViolation();
                        return false;
                    }
                } catch (final Exception e) {
                    context.buildConstraintViolationWithTemplate(
                            "Invalid filter value `" + tokens[2] + "` at position `" + i +
                                    "`. Score must be a valid, positive integer.")
                            .addConstraintViolation();
                    return false;
                }
            }
            if (tokens[0].equals("inspection_date")) {
                try {
                    LocalDateTime.parse(tokens[2]);
                } catch (final Exception e) {
                    context.buildConstraintViolationWithTemplate(
                            "Invalid filter value `" + tokens[2] + "` at position `" + i +
                                    "`. Inspection date must be a valid date.")
                            .addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }
}
