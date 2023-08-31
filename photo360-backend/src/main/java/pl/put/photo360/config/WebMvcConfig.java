package pl.put.photo360.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import pl.put.photo360.interceptor.ApiKeyInterceptor;
import pl.put.photo360.interceptor.TokenValidatorInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
    private final ApiKeyInterceptor apiKeyInterceptor;
    private final TokenValidatorInterceptor tokenValidatorInterceptor;

    @Autowired
    public WebMvcConfig( ApiKeyInterceptor apiKeyInterceptor,
        TokenValidatorInterceptor aTokenValidatorInterceptor )
    {
        this.apiKeyInterceptor = apiKeyInterceptor;
        tokenValidatorInterceptor = aTokenValidatorInterceptor;
    }

    @Override
    public void addInterceptors( InterceptorRegistry registry )
    {
        registry.addInterceptor( apiKeyInterceptor )
            .addPathPatterns( "/photo360/**" );
        registry.addInterceptor( tokenValidatorInterceptor )
            .addPathPatterns( "/photo360/**" )
            .excludePathPatterns( "/photo360/authorization/logIn" )
            .excludePathPatterns( "/photo360/authorization/register" );
    }

    @Override
    public void addCorsMappings( CorsRegistry registry )
    {
        registry.addMapping( "/photo360/**" )
            .allowedMethods( "*" )
            .allowedOrigins( "*" )
            .allowedHeaders( "*" );
    }
}
