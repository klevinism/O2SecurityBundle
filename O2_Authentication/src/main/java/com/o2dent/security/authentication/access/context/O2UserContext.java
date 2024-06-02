package com.o2dent.security.authentication.access.context;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.ws.rs.NameBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NameBinding
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.CONSTRUCTOR})
@Constraint(validatedBy = O2UserContextConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface O2UserContext{
    String message() default "You do not have the right authority!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] roles() default {"ROLE_oxydent-user"};
}
