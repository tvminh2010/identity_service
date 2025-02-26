package zve.com.vn.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint {

  int min() default 18;

  String message() default "Date of birth must be between {min} and {max} years old!";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
