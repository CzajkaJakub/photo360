package pl.put.photo360.exception;

import lombok.Getter;
import pl.put.photo360.dto.ServerResponseCode;

@Getter
public class FieldValidationException extends RuntimeException
{
    private final ServerResponseCode serverResponseCode;

    public FieldValidationException( ServerResponseCode aServerResponseCode )
    {
        super( aServerResponseCode.getResponseMessage() );
        serverResponseCode = aServerResponseCode;
    }
}
