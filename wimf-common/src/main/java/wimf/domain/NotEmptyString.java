package wimf.domain;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = NotEmptyStringValidator.class)
@Documented
public @interface NotEmptyString {

    String message() default "not an empty string";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
