package pl.put.photo360.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import pl.put.photo360.interceptor.ApiKeyInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
    private final ApiKeyInterceptor apiKeyInterceptor;

    @Autowired
    public WebMvcConfig( ApiKeyInterceptor apiKeyInterceptor )
    {
        this.apiKeyInterceptor = apiKeyInterceptor;
    }

    @Override
    public void addInterceptors( InterceptorRegistry registry )
    {
        registry.addInterceptor( apiKeyInterceptor )
            .addPathPatterns( "/photo360/**" );
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
