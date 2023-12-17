package pl.put.photo360.interceptors;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import pl.put.photo360.config.ConfigURL;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.handlers.AuthHandler;

@Component
public class RestTemplateFilter implements ClientHttpRequestInterceptor
{
    private final Configuration configuration;
    private final ConfigURL configURL;
    private final AuthHandler authHandler;

    @Autowired
    public RestTemplateFilter( Configuration configuration, AuthHandler authHandler, ConfigURL configURL )
    {
        this.configuration = configuration;
        this.configURL = configURL;
        this.authHandler = authHandler;
    }

    @Override
    public ClientHttpResponse intercept( HttpRequest request, byte[] body,
        ClientHttpRequestExecution execution ) throws IOException
    {
        request.getHeaders()
            .set( "publicApiKey", configuration.getPUBLIC_API_KEY() );

        if ( authHandler.getToken() != null )
        {
            request.getHeaders()
                    .set("Authorization", authHandler.getToken());
        }

        if (request.getURI().equals(configURL.getUPLOAD_PHOTOS_URL())) {
            request.getHeaders().setContentType(MediaType.MULTIPART_FORM_DATA);
        }
        return execution.execute( request, body );
    }
}
