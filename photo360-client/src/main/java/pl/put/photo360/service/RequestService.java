package pl.put.photo360.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class RequestService
{
    private final RestTemplate restTemplate;

    @Autowired
    public RequestService(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    // TODO - serwer nie odpowiada tak jak potrzeba na confirmationResetPassword
    public <T, R> R executeRequest(T requestDto, String endpointUrl, Class<R> responseType) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<T> request = new HttpEntity<>(requestDto, headers);
        try {
            return restTemplate.postForObject(endpointUrl, request, responseType);
        } catch (Exception e) {
            throw new IOException(e.getCause());
        }
    }
}
