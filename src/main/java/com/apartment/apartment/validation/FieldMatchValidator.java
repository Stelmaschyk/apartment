package com.apartment.apartment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstField;
    private String repeatedField;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstField = constraintAnnotation.firstField();
        this.repeatedField = constraintAnnotation.repeatedField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object fieldValue = new BeanWrapperImpl(value)
                .getPropertyValue(firstField);
        Object fieldMatchValue = new BeanWrapperImpl(value)
                .getPropertyValue(repeatedField);
        return Objects.equals(fieldValue, fieldMatchValue);
    }
}
