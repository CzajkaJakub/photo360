package pl.put.photo360.shared.api;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Documented
@Constraint( validatedBy = PublicKeyValidator.class )
@Target(
{ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER } )
@Retention( RetentionPolicy.RUNTIME )
public @interface PublicKeyConstraint
{
    String message() default "";

    Class< ? >[] groups() default
    {};

    Class< ? extends Payload>[] payload() default
    {};
}