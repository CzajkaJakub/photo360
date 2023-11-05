package pl.put.photo360.exception;

import lombok.Getter;
import pl.put.photo360.dto.ServerResponseCode;

@Getter
public class UnauthorizedRoleException extends RuntimeException
{
    private final ServerResponseCode serverResponseCode;

    public UnauthorizedRoleException( ServerResponseCode aServerResponseCode )
    {
        super( aServerResponseCode.getResponseMessage() );
        serverResponseCode = aServerResponseCode;
    }
}