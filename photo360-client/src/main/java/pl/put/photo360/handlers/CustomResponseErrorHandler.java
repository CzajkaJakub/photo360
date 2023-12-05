package pl.put.photo360.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Stage;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import pl.put.photo360.dto.RequestResponseDto;

import java.io.IOException;

public class CustomResponseErrorHandler implements ResponseErrorHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = (HttpStatus) response.getStatusCode();
        return (statusCode.is4xxClientError() || statusCode.is5xxServerError());
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = (HttpStatus) response.getStatusCode();
        RequestResponseDto responseDto = objectMapper.readValue(response.getBody(), RequestResponseDto.class);

        System.out.println("Status code: " + responseDto.getStatusCode());
        System.out.println("Status type: " + responseDto.getStatusType());
        System.out.println("Response message: " + responseDto.getResponseMessage());

        // TODO obsłużyć 45 kodów błędów
        if (statusCode.is5xxServerError()) {
            System.out.println("Wystąpił błąd o kodzie 5xx");
        } else if (statusCode.is4xxClientError()) {
            System.out.println("Wystąpił błąd o kodzie 4xx");
            if (statusCode == HttpStatus.UNAUTHORIZED) {
                System.out.println("Wystąpił błąd o kodzie 401 - Nieautoryzowany dostęp");
            }
        }
    }
}
