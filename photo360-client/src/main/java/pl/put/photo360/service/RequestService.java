package pl.put.photo360.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.put.photo360.dto.*;
import pl.put.photo360.config.Configuration;

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

    public RequestResponseDto registerUser( RegisterRequestDto registerRequestDto ) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<RegisterRequestDto> request = new HttpEntity<>(registerRequestDto, headers);
        RequestResponseDto requestResponseDto = restTemplate.postForObject(
                configuration.getREGISTER_ENDPOINT_URL(),
                request,
                RequestResponseDto.class);
        return requestResponseDto;
    }

    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<LoginRequestDto> request = new HttpEntity<>(loginRequestDto, headers);
        LoginResponseDto loginResponseDto = restTemplate.postForObject(
                configuration.getLOGIN_ENDPOINT_URL(),
                request,
                LoginResponseDto.class);

        return loginResponseDto;
    }
}
