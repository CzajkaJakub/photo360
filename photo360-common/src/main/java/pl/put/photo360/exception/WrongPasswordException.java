package pl.put.photo360.exception;

import lombok.Getter;
import pl.put.photo360.dto.ServerResponseCode;

@Getter
public class WrongPasswordException extends RuntimeException
{
    private final ServerResponseCode serverResponseCode;

    public WrongPasswordException( ServerResponseCode aServerResponseCode )
    {
        super( aServerResponseCode.getResponseMessage() );
        serverResponseCode = aServerResponseCode;
    }
}