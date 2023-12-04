package pl.put.photo360.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.put.photo360.dto.LoginRequestDto;
import pl.put.photo360.dto.LoginResponseDto;
import pl.put.photo360.dto.RegisterRequestDto;
import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.config.Configuration;

@Component
public class RequestService
{
    private final Configuration configuration;

    @Autowired
    public RequestService(Configuration configuration)
    {
        this.configuration = configuration;
    }

    public RequestResponseDto registerUser( RegisterRequestDto registerRequestDto ) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("publicApiKey", configuration.getPUBLIC_API_KEY());
        HttpEntity<RegisterRequestDto> request = new HttpEntity<>(registerRequestDto, headers);
        RequestResponseDto requestResponseDto = restTemplate.postForObject(
                configuration.getREGISTER_ENDPOINT_URL(),
                request,
                RequestResponseDto.class);

        return requestResponseDto;
    }

    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("publicApiKey", configuration.getPUBLIC_API_KEY());
        HttpEntity<LoginRequestDto> request = new HttpEntity<>(loginRequestDto, headers);
        LoginResponseDto loginResponseDto = restTemplate.postForObject(
                configuration.getLOGIN_ENDPOINT_URL(),
                request,
                LoginResponseDto.class);

        return loginResponseDto;
    }
}
