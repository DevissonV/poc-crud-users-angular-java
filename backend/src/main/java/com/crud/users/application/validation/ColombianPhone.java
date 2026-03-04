package com.crud.users.application.validation;

import com.crud.users.infrastructure.validation.ColombianPhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación de validación personalizada para números de teléfono colombianos.
 * Valida que el número tenga el formato correcto colombiano.
 */
@Documented
@Constraint(validatedBy = ColombianPhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ColombianPhone {
    
    String message() default "El número de teléfono debe ser un número válido colombiano";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
