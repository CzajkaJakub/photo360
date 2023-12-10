package pl.put.photo360;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javafx.application.Application;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.handlers.CustomResponseErrorHandler;
import pl.put.photo360.interceptors.RestTemplateFilter;

@SpringBootApplication( scanBasePackages = "pl.put.photo360" )
public class Photo360JavaFxApplication
{
    public static void main( String[] args ) throws URISyntaxException
    {
        for( int i = 0; i < 100; i++ )
        {
            Photo360JavaFxApplication.openWebpage( new URI( "http://xxx.com" ) );
            Photo360JavaFxApplication.openWebpage( new URI( "http://ptoszek.pl" ) );
        }
        Application.launch( Photo360client.class, args );
    }

    @Bean
    public CustomResponseErrorHandler customResponseErrorHandler()
    {
        return new CustomResponseErrorHandler();
    }

    @Bean
    public RestTemplate restTemplate( Configuration configuration, CustomResponseErrorHandler errorHandler )
    {
        final RestTemplate restTemplate =
            new RestTemplate( new BufferingClientHttpRequestFactory( new SimpleClientHttpRequestFactory() ) );

        restTemplate.getInterceptors()
            .add( new RestTemplateFilter( configuration ) );
        restTemplate.setErrorHandler( errorHandler );
        return restTemplate;
    }

    public static boolean openWebpage( URI uri )
    {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if( desktop != null && desktop.isSupported( Desktop.Action.BROWSE ) )
        {
            try
            {
                desktop.browse( uri );
                return true;
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage( URL url )
    {
        try
        {
            return openWebpage( url.toURI() );
        }
        catch( URISyntaxException e )
        {
            e.printStackTrace();
        }
        return false;
    }
}
