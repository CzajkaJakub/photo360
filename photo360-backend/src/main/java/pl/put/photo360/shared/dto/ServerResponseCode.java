package pl.put.photo360.shared.dto;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

/**
 * All currently supported statuses which might be sent to user interface on webapp.
 */
public enum ServerResponseCode
{
    // @formatter:off
    STATUS_OK( "OK", HttpStatus.OK ),
    STATUS_CREATED( "CREATED", HttpStatus.CREATED ),
    STATUS_WRONG_PASSWORD("WRONG_PASSWORD", HttpStatus.UNAUTHORIZED),
    STATUS_PASSWORD_CAN_NOT_BE_THE_SAME("PASSWORD_CAN_NOT_BE_THE_SAME", HttpStatus.UNAUTHORIZED),
    STATUS_USER_NOT_FOUND_BY_LOGIN("USER_NOT_FOUND_BY_LOGIN", HttpStatus.NOT_FOUND),
    STATUS_WRONG_CREDENTIALS("WRONG_CREDENTIALS", HttpStatus.UNAUTHORIZED),
    STATUS_CONNECTION_REFUSED("CONNECTION_REFUSED", HttpStatus.NOT_FOUND),
    STATUS_USER_NOT_FOUND_BY_EMAIL("USER_NOT_FOUND_BY_EMAIL", HttpStatus.NOT_FOUND),
    STATUS_USER_CREATED("USER_CREATED", HttpStatus.CREATED),
    STATUS_USER_LOGGED("USER_LOGGED", HttpStatus.ACCEPTED),
    STATUS_PASSWORD_CHANGED("PASSWORD_CHANGED",HttpStatus.OK),
    STATUS_RESET_PASSWORD_REQUEST_EMAIL_SEND("RESET_PASSWORD_REQUEST_EMAIL_SEND",HttpStatus.OK),
    STATUS_WRONG_FIELD_SIZE("WRONG_FIELDS_SIZE", HttpStatus.NOT_ACCEPTABLE),
    STATUS_VALIDATION_NOT_PASSED("VALIDATION_NOT_PASSED", HttpStatus.NOT_ACCEPTABLE),
    STATUS_EMAIL_ALREADY_EXISTS("GIVEN_EMAIL_EXISTS", HttpStatus.FORBIDDEN),
    STATUS_LOGIN_ALREADY_EXISTS("GIVEN_LOGIN_EXISTS", HttpStatus.FORBIDDEN),
    STATUS_ACCOUNT_LOCKED( "ACCOUNT_LOCKED", HttpStatus.LOCKED ),
    STATUS_AUTH_TOKEN_EXPIRED("AUTH_TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED),
    STATUS_RESET_TOKEN_EXPIRED("RESET_TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED),
    STATUS_AUTH_TOKEN_NOT_VALID("AUTH_TOKEN_NOT_VALID", HttpStatus.UNAUTHORIZED),
    STATUS_USER_NOT_FOUND_FROM_TOKEN("USER_NOT_FOUND_FROM_TOKEN", HttpStatus.NOT_FOUND),
    STATUS_USER_NOT_FOUND_FROM_RESET_TOKEN("USER_NOT_FOUND_FROM_RESET_TOKEN", HttpStatus.NOT_FOUND),
    STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS("GIF_BY_GIVEN_ID_NOT_EXISTS", HttpStatus.NOT_FOUND),
    STATUS_GIF_IS_NOT_PUBLIC("GIF_IS_NOT_PUBLIC", HttpStatus.NOT_ACCEPTABLE),
    STATUS_WRONG_PUBLIC_API_KEY("WRONG_PUBLIC_API_KEY", HttpStatus.UNAUTHORIZED),
    STATUS_FIELD_CONTAINS_WHITESPACES("FIELD_CONTAINS_WHITESPACES", HttpStatus.NOT_ACCEPTABLE),
    STATUS_MISSING_REQUIRED_FIELD("STATUS_MISSING_REQUIRED_FIELD", HttpStatus.NOT_ACCEPTABLE),
    STATUS_EMAIL_WRONG_FORMAT("EMAIL_WRONG_FORMAT", HttpStatus.NOT_ACCEPTABLE),
    STATUS_UNSUPPORTED_FILE("UNSUPPORTED", HttpStatus.NOT_ACCEPTABLE),
    STATUS_WRONG_FILE_FORMAT("WRONG_FILE_FORMAT", HttpStatus.NOT_ACCEPTABLE),
    STATUS_UNAUTHORIZED_ROLE("UNAUTHORIZED_ROLE", HttpStatus.UNAUTHORIZED),
    STATUS_DELETE_NOT_ALLOWED("DELETE_NOT_ALLOWED", HttpStatus.UNAUTHORIZED),
    STATUS_ADD_TO_FAVOURITE_NOT_ALLOWED("ADD_TO_FAVOURITE_NOT_ALLOWED", HttpStatus.UNAUTHORIZED),
    STATUS_GIF_ALREADY_ADDED_TO_FAVOURITE("GIF_ALREADY_ADDED_TO_FAVOURITE", HttpStatus.METHOD_NOT_ALLOWED),
    STATUS_TOKEN_VALID("TOKEN_VALID", HttpStatus.OK),
    STATUS_PHOTO_UPLOADED("PHOTO_UPLOADED", HttpStatus.OK),
    STATUS_GIF_ADDED_TO_FAVOURITE("GIF_ADDED_TO_FAVOURITE", HttpStatus.OK),
    STATUS_GIF_REMOVED_FROM_FAVOURITE("GIF_REMOVED_FROM_FAVOURITE", HttpStatus.OK),
    STATUS_PASSWORD_EMAIL_FAILED("GIF_PASSWORD_EMAIL_FAILED", HttpStatus.CONFLICT),
    STATUS_GIF_REMOVED("GIF_REMOVED", HttpStatus.OK);
    // @formatter:on;

    private static final Map< String, ServerResponseCode > CODE_MAP = new HashMap<>();
    static
    {
        for( ServerResponseCode code : values() )
        {
            CODE_MAP.put( code.responseMessage, code );
        }
    }

    private final HttpStatus status;
    private final String responseMessage;

    ServerResponseCode( String aResponseMessage, HttpStatus aStatus )
    {
        status = aStatus;
        responseMessage = aResponseMessage;
    }

    /**
     * Gets {@link ServerResponseCode} from a given message.
     *
     * @param aCodeMessage
     *                         the message
     * @return
     */
    public static ServerResponseCode get( String aCodeMessage )
    {
        var statusCode = CODE_MAP.get( aCodeMessage );
        if( statusCode == null )
        {
            throw new IllegalArgumentException(
                String.format( "No code with message %s found", aCodeMessage ) );
        }
        else
        {
            return statusCode;
        }
    }

    public HttpStatus getStatus()
    {
        return this.status;
    }

    public String getResponseMessage()
    {
        return this.responseMessage;
    }
}
