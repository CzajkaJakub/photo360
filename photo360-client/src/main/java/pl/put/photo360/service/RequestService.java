package pl.put.photo360.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.web.client.RestTemplate;
import pl.put.photo360.dto.LoginRequestDto;
import pl.put.photo360.dto.LoginResponseDto;
import pl.put.photo360.dto.RegisterRequestDto;
import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.config.Configuration;

import javax.print.CancelablePrintJob;

@Component
public class RequestService
{
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final Configuration configuration;

    @Autowired
    public RequestService(OkHttpClient okHttpClient, ObjectMapper objectMapper, Configuration configuration)
    {
        this.client = okHttpClient;
        this.objectMapper = objectMapper;
        this.configuration = configuration;
    }

    public RequestResponseDto registerUser( RegisterRequestDto registerRequestDto ) throws Exception {
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
