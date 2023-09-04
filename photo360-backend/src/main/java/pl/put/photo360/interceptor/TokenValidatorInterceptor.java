package pl.put.photo360.interceptor;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.put.photo360.shared.utils.JwtValidator;

@Component
public class TokenValidatorInterceptor implements HandlerInterceptor
{

    private final JwtValidator jwtValidator;

    @Autowired
    public TokenValidatorInterceptor( JwtValidator aJwtValidator )
    {
        jwtValidator = aJwtValidator;
    }

    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler )
        throws Exception
    {
        if( Objects.equals( request.getMethod(), "OPTIONS" ) )
            return true; // Ignore preflight request of cors
        jwtValidator.validateJwtToken( request.getHeader( HttpHeaders.AUTHORIZATION ) );
        return HandlerInterceptor.super.preHandle( request, response, handler );
    }

    @Override
    public void postHandle( HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView ) throws Exception
    {
        HandlerInterceptor.super.postHandle( request, response, handler, modelAndView );
    }

    @Override
    public void afterCompletion( HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex ) throws Exception
    {
        HandlerInterceptor.super.afterCompletion( request, response, handler, ex );
    }
}
