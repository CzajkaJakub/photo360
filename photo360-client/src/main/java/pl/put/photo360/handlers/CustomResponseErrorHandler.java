package pl.put.photo360.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import pl.put.photo360.dto.RequestResponseDto;

import java.io.IOException;

public class CustomResponseErrorHandler implements ResponseErrorHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = (HttpStatus) response.getStatusCode();
        return (statusCode.is3xxRedirection() || statusCode.is4xxClientError() || statusCode.is5xxServerError());
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        RequestResponseDto responseDto = objectMapper.readValue(response.getBody(), RequestResponseDto.class);
        String responseMessage = responseDto.getResponseMessage();

        throw new IOException(
                switch (responseMessage) {
                    case "USER_NOT_FOUND_BY_LOGIN" -> "Nie znaleziono użytkownika o podanym loginie";
                    case "WRONG_FIELDS_SIZE" -> "Co najmniej jedno z pól jest nieprawidłowe";
                    case "WRONG_CREDENTIALS" -> "ERROR"; // TODO
                    case "AUTH_TOKEN_EXPIRED" -> "Token autoryzacyjny wygasł";
                    case "RESET_TOKEN_EXPIRED" -> "Token resetowania hasła wygasł";
                    case "AUTH_TOKEN_NOT_VALID" -> "Nieprawidłowy token autoryzacyjny";
                    case "WRONG_PUBLIC_API_KEY" -> "Nieprawidłowy klucz publiczny";
                    case "UNAUTHORIZED_ROLE" -> "Nie posiadasz uprawnień do tej akcji"; // TODO
                    case "DELETE_NOT_ALLOWED" -> "Nie można usunąć elementu";
                    case "ADD_TO_FAVOURITE_NOT_ALLOWED" -> "Nie można dodać elementu do ulubionych";
                    case "PASSWORD_CAN_NOT_BE_THE_SAME" -> "Nowe hasło nie może być identyczne";
                    case "VALIDATION_NOT_PASSED" -> "Walidacja nie powiodła się"; // TODO
                    case "EMAIL_ALREADY_VERIFIED" -> "Adres email został już zweryfikowany";
                    case "EMAIL_VERIFICATION_TOKEN_NOT_VALID" -> "Token weryfikacyjny jest nieprawidłowy";
                    case "EMAIL_VERIFICATION_TOKEN_EXPIRED" -> "Token weryfikacyjny wygasł";
                    case "GIF_IS_NOT_PUBLIC" -> "GIF nie jest publiczny"; // TODO
                    case "FIELD_CONTAINS_WHITESPACES" -> "Co najmniej jedno pole zawiera spacje"; // TODO
                    case "STATUS_MISSING_REQUIRED_FIELD" -> "Proszę uzupełnić wszystkie pola"; // TODO
                    case "EMAIL_WRONG_FORMAT" -> "Niepoprawny format adresu email";
                    case "UNSUPPORTED" -> "Nie wspierane"; // TODO
                    case "WRONG_FILE_FORMAT" -> "Niepoprawny format danych";
                    case "CONNECTION_REFUSED" -> "Połączenie zerwane"; // TODO
                    case "USER_NOT_FOUND_BY_EMAIL" -> "Nie znaleziono użytkownika o podanym adresie email";
                    case "USER_NOT_FOUND_FROM_TOKEN" -> "Nie znaleziono użytkownika o podanym tokenie"; // TODO
                    case "GIF_BY_GIVEN_ID_NOT_EXISTS" -> "GIF o podanym ID nie istnieje";
                    case "GIVEN_EMAIL_EXISTS" -> "Podany adres email jest już używany";
                    case "GIVEN_LOGIN_EXISTS" -> "Podana nazwa użytkownika jest już używana";
                    case "ACCOUNT_LOCKED" -> "Konto zostało zablokowane z powodu zbyt wielu nieudanych prób logowania";
                    case "EMAIL_NOT_CONFIRMED" -> "Akcja niedostępna z powodu niepotwierdzonego adresu email"; // TODO
                    case "EMAIL_SEND_FAILED" -> "Wysłanie wiadomości email nie powiodło się"; // TODO
                    case "GIF_ALREADY_ADDED_TO_FAVOURITE" -> "GIF został już dodany do ulubionych";
                    default -> "Nieznany błąd"; // TODO
                });
    }
}
