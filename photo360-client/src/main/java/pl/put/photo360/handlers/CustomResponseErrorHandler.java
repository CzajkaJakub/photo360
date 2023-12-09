package pl.put.photo360.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.dto.ToastsConstants;

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
                    case "USER_NOT_FOUND_BY_LOGIN" -> ToastsConstants.USER_NOT_FOUND_BY_LOGIN.getPath();
                    case "WRONG_FIELDS_SIZE" -> ToastsConstants.WRONG_FIELDS_SIZE.getPath();
                    case "WRONG_CREDENTIALS" -> ToastsConstants.WRONG_CREDENTIALS.getPath();
                    case "AUTH_TOKEN_EXPIRED" -> ToastsConstants.AUTH_TOKEN_EXPIRED.getPath();
                    case "RESET_TOKEN_EXPIRED" -> ToastsConstants.RESET_TOKEN_EXPIRED.getPath();
                    case "AUTH_TOKEN_NOT_VALID" -> ToastsConstants.AUTH_TOKEN_NOT_VALID.getPath();
                    case "WRONG_PUBLIC_API_KEY" -> ToastsConstants.WRONG_PUBLIC_API_KEY.getPath();
                    case "UNAUTHORIZED_ROLE" -> ToastsConstants.UNAUTHORIZED_ROLE.getPath();
                    case "DELETE_NOT_ALLOWED" -> ToastsConstants.DELETE_NOT_ALLOWED.getPath();
                    case "ADD_TO_FAVOURITE_NOT_ALLOWED" -> ToastsConstants.ADD_TO_FAVOURITE_NOT_ALLOWED.getPath();
                    case "PASSWORD_CAN_NOT_BE_THE_SAME" -> ToastsConstants.PASSWORD_CAN_NOT_BE_THE_SAME.getPath();
                    case "VALIDATION_NOT_PASSED" -> ToastsConstants.VALIDATION_NOT_PASSED.getPath();
                    case "EMAIL_ALREADY_VERIFIED" -> ToastsConstants.EMAIL_ALREADY_VERIFIED.getPath();
                    case "EMAIL_VERIFICATION_TOKEN_NOT_VALID" -> ToastsConstants.EMAIL_VERIFICATION_TOKEN_NOT_VALID.getPath();
                    case "EMAIL_VERIFICATION_TOKEN_EXPIRED" -> ToastsConstants.EMAIL_VERIFICATION_TOKEN_EXPIRED.getPath();
                    case "GIF_IS_NOT_PUBLIC" -> ToastsConstants.GIF_IS_NOT_PUBLIC.getPath();
                    case "FIELD_CONTAINS_WHITESPACES" -> ToastsConstants.FIELD_CONTAINS_WHITESPACES.getPath();
                    case "STATUS_MISSING_REQUIRED_FIELD" -> ToastsConstants.STATUS_MISSING_REQUIRED_FIELD.getPath();
                    case "EMAIL_WRONG_FORMAT" -> ToastsConstants.EMAIL_WRONG_FORMAT.getPath();
                    case "UNSUPPORTED" -> ToastsConstants.UNSUPPORTED.getPath();
                    case "WRONG_FILE_FORMAT" -> ToastsConstants.WRONG_FILE_FORMAT.getPath();
                    case "CONNECTION_REFUSED" -> ToastsConstants.CONNECTION_REFUSED.getPath();
                    case "USER_NOT_FOUND_BY_EMAIL" -> ToastsConstants.USER_NOT_FOUND_BY_EMAIL.getPath();
                    case "USER_NOT_FOUND_FROM_TOKEN" -> ToastsConstants.USER_NOT_FOUND_FROM_TOKEN.getPath();
                    case "GIF_BY_GIVEN_ID_NOT_EXISTS" -> ToastsConstants.GIF_BY_GIVEN_ID_NOT_EXISTS.getPath();
                    case "GIVEN_EMAIL_EXISTS" -> ToastsConstants.GIVEN_EMAIL_EXISTS.getPath();
                    case "GIVEN_LOGIN_EXISTS" -> ToastsConstants.GIVEN_LOGIN_EXISTS.getPath();
                    case "ACCOUNT_LOCKED" -> ToastsConstants.ACCOUNT_LOCKED.getPath();
                    case "EMAIL_NOT_CONFIRMED" -> ToastsConstants.EMAIL_NOT_CONFIRMED.getPath();
                    case "EMAIL_SEND_FAILED" -> ToastsConstants.EMAIL_SEND_FAILED.getPath();
                    case "GIF_ALREADY_ADDED_TO_FAVOURITE" -> ToastsConstants.GIF_ALREADY_ADDED_TO_FAVOURITE.getPath();
                    default -> ToastsConstants.DEFAULT_ERROR.getPath();
                });
    }
}
