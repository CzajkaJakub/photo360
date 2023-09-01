package pl.put.photo360.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.put.photo360.shared.api.PublicKeyValidator;

import java.util.Objects;

@Service
public class ApiKeyInterceptor implements HandlerInterceptor
{
    private final PublicKeyValidator publicKeyValidator;

    @Autowired
    public ApiKeyInterceptor( PublicKeyValidator aPublicKeyValidator )
    {
        publicKeyValidator = aPublicKeyValidator;
    }

    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler )
        throws Exception
    {
        if( Objects.equals( request.getMethod(), "OPTIONS" ) )
            return true; // Ignore preflight request of cors
        publicKeyValidator.isValid( request.getHeader( "publicApiKey" ) );
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
