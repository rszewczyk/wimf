package wimf.domain;

import com.google.common.base.Strings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * javax.validation wrapper around {@link Strings#isNullOrEmpty(String)}
 *
 * based off of:
 *   https://github.com/gilday/mobtown/blob/master/mobtown-common/src/main/java/mobtown/domain/NotEmptyString.java
 *   (MIT License)
 */
public class NotEmptyStringValidator implements ConstraintValidator<NotEmptyString, String> {

    @Override
    public void initialize(final NotEmptyString constraintAnnotation) {
        // nop
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return !Strings.isNullOrEmpty(value);
    }
}
