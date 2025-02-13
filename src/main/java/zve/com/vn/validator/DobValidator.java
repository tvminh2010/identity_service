package zve.com.vn.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate>{

	private int min;
	private int max;
	/* ------------------------------------------------------------------------ */
	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
		
		if(Objects.isNull(value)) {
			return true;
		}
		return  ChronoUnit.YEARS.between(value, LocalDate.now()) >= min & ChronoUnit.YEARS.between(value, LocalDate.now()) <= max ;
	}
	/* ------------------------------------------------------------------------ */
	@Override
	public void initialize(DobConstraint constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
		min = constraintAnnotation.min();
		max = constraintAnnotation.max();
	}
	/* ------------------------------------------------------------------------ */
}
