package pl.put.photo360.tokenValidator.aspect;

import java.util.Objects;

import org.apache.hc.core5.http.HttpHeaders;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import pl.put.photo360.dto.UserRoles;
import pl.put.photo360.shared.utils.JwtValidator;
import pl.put.photo360.tokenValidator.annotation.RequiredRole;

@Aspect
@Component
public class AccessControlAspect
{
    private final JwtValidator jwtValidator;

    @Autowired
    public AccessControlAspect( JwtValidator aJwtValidator )
    {
        jwtValidator = aJwtValidator;
    }

    @Before( "@annotation(pl.put.photo360.tokenValidator.annotation.RequiredRole)" )
    public void before( JoinPoint joinPoint )
    {
        MethodSignature ms = (MethodSignature)joinPoint.getSignature();
        UserRoles requiredRole = ms.getMethod()
            .getAnnotation( RequiredRole.class )
            .role();

        HttpServletRequest request =
            ((ServletRequestAttributes)Objects.requireNonNull( RequestContextHolder.getRequestAttributes() ))
                .getRequest();
        String authToken = request.getHeader( HttpHeaders.AUTHORIZATION );
        jwtValidator.validateJwtTokenWithRoles( authToken, requiredRole );
    }
}
