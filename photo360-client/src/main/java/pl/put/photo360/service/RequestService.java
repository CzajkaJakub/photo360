package pl.put.photo360.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.put.photo360.dto.*;
import pl.put.photo360.config.Configuration;

import java.io.IOException;

@Component
public class RequestService
{
    private final Configuration configuration;
    private final RestTemplate restTemplate;

    @Autowired
    public RequestService(Configuration configuration, RestTemplate restTemplate)
    {
        this.configuration = configuration;
        this.restTemplate = restTemplate;
    }

    public RequestResponseDto registerUser( RegisterRequestDto registerRequestDto ) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<RegisterRequestDto> request = new HttpEntity<>(registerRequestDto, headers);
        try {
            return restTemplate.postForObject(
                    configuration.getREGISTER_ENDPOINT_URL(),
                    request,
                    RequestResponseDto.class);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<LoginRequestDto> request = new HttpEntity<>(loginRequestDto, headers);
        try {
            return restTemplate.postForObject(
                    configuration.getLOGIN_ENDPOINT_URL(),
                    request,
                    LoginResponseDto.class);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
