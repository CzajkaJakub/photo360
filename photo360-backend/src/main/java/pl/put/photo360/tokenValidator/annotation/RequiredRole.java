package pl.put.photo360.tokenValidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pl.put.photo360.dto.UserRoles;

@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
public @interface RequiredRole
{
    UserRoles role();
}