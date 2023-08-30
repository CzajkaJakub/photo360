package pl.put.photo360.shared.exception;

import lombok.Getter;
import pl.put.photo360.shared.dto.ServerResponseCode;

@Getter
public class AccountLockedException extends RuntimeException
{
    private final ServerResponseCode serverResponseCode;

    public AccountLockedException( ServerResponseCode aServerResponseCode )
    {
        super( aServerResponseCode.getResponseMessage() );
        serverResponseCode = aServerResponseCode;
    }
}
