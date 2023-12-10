package pl.put.photo360.service;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.toast.Toast;

@Component
public class RequestService
{
    private final RestTemplate restTemplate;

    @Autowired
    public RequestService( RestTemplate restTemplate )
    {
        this.restTemplate = restTemplate;
    }

    public <R> R executeGetRequest(ActionEvent event, String endpointUrl, Class<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers); // Brak ciała żądania dla GET
        try {
            return restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, responseType).getBody();
        } catch (Exception e) {
            Toast.showToast(event, (IOException)e.getCause());
            return null;
        }
    }

    // TODO - serwer nie odpowiada tak jak potrzeba na confirmationResetPassword
    public < T, R > R executeRequest( ActionEvent event, T requestDto, String endpointUrl,
        Class< R > responseType )
    {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity< T > request = new HttpEntity<>( requestDto, headers );
        try
        {
            return restTemplate.postForObject( endpointUrl, request, responseType );
        }
        catch( Exception e )
        {
            Toast.showToast( event, (IOException)e.getCause() );
            return null;
        }
    }

    public <T, R> R executeRequest(ActionEvent event, T requestDto, String endpointUrl,
                                   Class<R> responseType, HttpMethod httpMethod) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<T> request = new HttpEntity<>(requestDto, headers);
        try {
            ResponseEntity<R> response = restTemplate.exchange(endpointUrl, httpMethod, request, responseType);
            return response.getBody();
        } catch (Exception e) {
            Toast.showToast(event, (IOException)e.getCause());
            return null;
        }
    }


    public < T > T createRequest( Class< T > dtoClass, TextField ... fields )
    {
        try
        {
            Constructor< ? >[] constructors = dtoClass.getDeclaredConstructors();
            for( Constructor< ? > constructor : constructors )
            {
                if( constructor.getParameterCount() == fields.length )
                {
                    Object[] args = Arrays.stream( fields )
                        .map( field -> !field.getText()
                            .isBlank() ? field.getText() : null )
                        .toArray();
                    return (T)constructor.newInstance( args );
                }
            }
        }
        catch( InstantiationException | IllegalAccessException | InvocationTargetException e )
        {
            e.printStackTrace(); // Obsługa wyjątku
        }
        return null;
    }
}
