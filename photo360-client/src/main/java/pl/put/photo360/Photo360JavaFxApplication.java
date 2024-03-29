package pl.put.photo360;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javafx.application.Application;
import pl.put.photo360.camera.view.CameraWindow;
import pl.put.photo360.config.ConfigURL;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.handlers.CustomResponseErrorHandler;
import pl.put.photo360.interceptors.RestTemplateFilter;

@SpringBootApplication( scanBasePackages = "pl.put.photo360" )
public class Photo360JavaFxApplication
{
    public static void main( String[] args )
    {
        Application.launch( Photo360client.class, args );
    }

    @Bean
    public CustomResponseErrorHandler customResponseErrorHandler()
    {
        return new CustomResponseErrorHandler();
    }

    @Bean
    public RestTemplate restTemplate( Configuration configuration, CustomResponseErrorHandler errorHandler,
        AuthHandler authHandler, ConfigURL configURL )
    {
        final RestTemplate restTemplate =
            new RestTemplate( new BufferingClientHttpRequestFactory( new SimpleClientHttpRequestFactory() ) );

        restTemplate.getInterceptors()
            .add( new RestTemplateFilter( configuration, authHandler, configURL ) );
        restTemplate.setErrorHandler( errorHandler );
        return restTemplate;
    }

    @Bean
    public CameraWindow cameraWindow()
    {
        return new CameraWindow();
    }
}
