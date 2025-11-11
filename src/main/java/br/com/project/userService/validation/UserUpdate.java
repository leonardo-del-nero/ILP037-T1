package br.com.project.userService.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = UserUpdateImpl.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserUpdate {
    String message() default "Error Updating";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};
}
