package wimf.domain;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class ValidationException extends RuntimeException {
    private ValidationException(final String message) {
        super(message);
    }

    public static <T> ValidationException from(Set<ConstraintViolation<T>> violations) {
        StringBuilder stringBuilder = new StringBuilder().append("Validation failed: ");

        violations.forEach(v ->
            stringBuilder
                    .append("Property ")
                    .append(v.getPropertyPath())
                    .append(" of ")
                    .append(v.getRootBeanClass())
                    .append(" with value ")
                    .append(v.getInvalidValue())
                    .append(" failed constraint ")
                    .append(v.getMessage())
                    .append("; ")
        );

        return new ValidationException(stringBuilder.toString());
    }
}
