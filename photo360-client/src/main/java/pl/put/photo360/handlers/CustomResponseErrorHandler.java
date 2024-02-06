package pl.put.photo360.handlers;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.dto.ToastsConstants;

public class CustomResponseErrorHandler implements ResponseErrorHandler
{
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError( ClientHttpResponse response ) throws IOException
    {
        HttpStatus statusCode = (HttpStatus)response.getStatusCode();
        return (statusCode.is3xxRedirection() || statusCode.is4xxClientError()
            || statusCode.is5xxServerError());
    }

    @Override
    public void handleError( ClientHttpResponse response ) throws IOException
    {
        RequestResponseDto responseDto =
            objectMapper.readValue( response.getBody(), RequestResponseDto.class );
        String responseMessage = responseDto.getResponseMessage();

        throw new IOException( switch( responseMessage )
        {
            case "USER_NOT_FOUND_BY_LOGIN" -> ToastsConstants.USER_NOT_FOUND_BY_LOGIN.getMessage();
            case "WRONG_FIELDS_SIZE" -> ToastsConstants.WRONG_FIELDS_SIZE.getMessage();
            case "WRONG_CREDENTIALS" -> ToastsConstants.WRONG_CREDENTIALS.getMessage();
            case "AUTH_TOKEN_EXPIRED" -> ToastsConstants.AUTH_TOKEN_EXPIRED.getMessage();
            case "RESET_TOKEN_EXPIRED" -> ToastsConstants.RESET_TOKEN_EXPIRED.getMessage();
            case "AUTH_TOKEN_NOT_VALID" -> ToastsConstants.AUTH_TOKEN_NOT_VALID.getMessage();
            case "WRONG_PUBLIC_API_KEY" -> ToastsConstants.WRONG_PUBLIC_API_KEY.getMessage();
            case "UNAUTHORIZED_ROLE" -> ToastsConstants.UNAUTHORIZED_ROLE.getMessage();
            case "DELETE_NOT_ALLOWED" -> ToastsConstants.DELETE_NOT_ALLOWED.getMessage();
            case "ADD_TO_FAVOURITE_NOT_ALLOWED" -> ToastsConstants.ADD_TO_FAVOURITE_NOT_ALLOWED.getMessage();
            case "PASSWORD_CAN_NOT_BE_THE_SAME" -> ToastsConstants.PASSWORD_CAN_NOT_BE_THE_SAME.getMessage();
            case "VALIDATION_NOT_PASSED" -> ToastsConstants.VALIDATION_NOT_PASSED.getMessage();
            case "EMAIL_ALREADY_VERIFIED" -> ToastsConstants.EMAIL_ALREADY_VERIFIED.getMessage();
            case "EMAIL_VERIFICATION_TOKEN_NOT_VALID" ->
                ToastsConstants.EMAIL_VERIFICATION_TOKEN_NOT_VALID.getMessage();
            case "EMAIL_VERIFICATION_TOKEN_EXPIRED" ->
                ToastsConstants.EMAIL_VERIFICATION_TOKEN_EXPIRED.getMessage();
            case "GIF_IS_NOT_PUBLIC" -> ToastsConstants.GIF_IS_NOT_PUBLIC.getMessage();
            case "FIELD_CONTAINS_WHITESPACES" -> ToastsConstants.FIELD_CONTAINS_WHITESPACES.getMessage();
            case "STATUS_MISSING_REQUIRED_FIELD" ->
                ToastsConstants.STATUS_MISSING_REQUIRED_FIELD.getMessage();
            case "EMAIL_WRONG_FORMAT" -> ToastsConstants.EMAIL_WRONG_FORMAT.getMessage();
            case "UNSUPPORTED" -> ToastsConstants.UNSUPPORTED.getMessage();
            case "WRONG_FILE_FORMAT" -> ToastsConstants.WRONG_FILE_FORMAT.getMessage();
            case "CONNECTION_REFUSED" -> ToastsConstants.CONNECTION_REFUSED.getMessage();
            case "USER_NOT_FOUND_BY_EMAIL" -> ToastsConstants.USER_NOT_FOUND_BY_EMAIL.getMessage();
            case "USER_NOT_FOUND_FROM_TOKEN" -> ToastsConstants.USER_NOT_FOUND_FROM_TOKEN.getMessage();
            case "GIF_BY_GIVEN_ID_NOT_EXISTS" -> ToastsConstants.GIF_BY_GIVEN_ID_NOT_EXISTS.getMessage();
            case "GIVEN_EMAIL_EXISTS" -> ToastsConstants.GIVEN_EMAIL_EXISTS.getMessage();
            case "GIVEN_LOGIN_EXISTS" -> ToastsConstants.GIVEN_LOGIN_EXISTS.getMessage();
            case "ACCOUNT_LOCKED" -> ToastsConstants.ACCOUNT_LOCKED.getMessage();
            case "EMAIL_NOT_CONFIRMED" -> ToastsConstants.EMAIL_NOT_CONFIRMED.getMessage();
            case "EMAIL_SEND_FAILED" -> ToastsConstants.EMAIL_SEND_FAILED.getMessage();
            case "GIF_ALREADY_ADDED_TO_FAVOURITE" ->
                ToastsConstants.GIF_ALREADY_ADDED_TO_FAVOURITE.getMessage();
            default -> ToastsConstants.DEFAULT_ERROR.getMessage();
        } );
    }
}
