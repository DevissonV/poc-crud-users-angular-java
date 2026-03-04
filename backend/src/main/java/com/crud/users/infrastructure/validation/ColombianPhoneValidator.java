package com.crud.users.infrastructure.validation;

import com.crud.users.application.validation.ColombianPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validador para números de teléfono colombianos.
 * Acepta formatos como: +57 300 123 4567, 573001234567, 300 123 4567, etc.
 */
public class ColombianPhoneValidator implements ConstraintValidator<ColombianPhone, String> {
    
    // Regex para teléfonos colombianos (móviles que empiezan con 3)
    private static final String PHONE_PATTERN = "^\\+?57[\\s-]?3[0-9]{2}[\\s-]?\\d{3}[\\s-]?\\d{4}$";
    private static final Pattern pattern = Pattern.compile(PHONE_PATTERN);

    @Override
    public void initialize(ColombianPhone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        // Permitir valores nulos (usar @NotNull si es obligatorio)
        if (phone == null || phone.isEmpty()) {
            return true;
        }
        
        return pattern.matcher(phone).matches();
    }
}
