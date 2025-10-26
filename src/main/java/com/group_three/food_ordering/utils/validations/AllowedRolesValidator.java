package com.group_three.food_ordering.utils.validations;

import com.group_three.food_ordering.enums.RoleType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class AllowedRolesValidator implements ConstraintValidator<AllowedRoles, RoleType> {

    private Set<RoleType> allowed;

    @Override
    public void initialize(AllowedRoles constraint) {
        allowed = Set.of(constraint.value());
    }

    @Override
    public boolean isValid(RoleType value, ConstraintValidatorContext context) {
        return value != null && allowed.contains(value);
    }
}

