package pl.put.photo360.shared.api;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

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

    Class< ? extends Payload >[] payload() default
    {};
}