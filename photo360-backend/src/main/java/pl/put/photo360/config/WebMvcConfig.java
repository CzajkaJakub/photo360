package pl.put.photo360.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
    @Override
    public void addCorsMappings( CorsRegistry registry )
    {
        registry.addMapping( "/photo360/**" )
            .allowedMethods( "*" )
            .allowedOrigins( "*" )
            .allowedHeaders( "*" );
    }
}
