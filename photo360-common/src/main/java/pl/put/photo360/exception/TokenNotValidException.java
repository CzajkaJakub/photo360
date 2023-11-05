package pl.put.photo360.exception;

import lombok.Getter;
import pl.put.photo360.dto.ServerResponseCode;

@Getter
public class TokenNotValidException extends RuntimeException
{
    private final ServerResponseCode serverResponseCode;

    public TokenNotValidException( ServerResponseCode aServerResponseCode )
    {
        super( aServerResponseCode.getResponseMessage() );
        serverResponseCode = aServerResponseCode;
    }
}