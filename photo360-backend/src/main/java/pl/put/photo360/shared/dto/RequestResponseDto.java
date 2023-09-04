package pl.put.photo360.shared.dto;

import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple class which contains a message as response to frontend.
 */
@Getter
@Setter
@NoArgsConstructor
public class RequestResponseDto
{
    private String responseMessage;
    private String statusType;
    private int statusCode;

    public RequestResponseDto( ServerResponseCode aServerResponse )
    {
        statusCode = aServerResponse.getStatus()
            .value();
        statusType = aServerResponse.getStatus()
            .getReasonPhrase();
        responseMessage = aServerResponse.getResponseMessage();
    }

    @Override
    public boolean equals( Object aO )
    {
        if( this == aO )
            return true;
        if( !(aO instanceof RequestResponseDto that) )
            return false;
        return getStatusCode() == that.getStatusCode()
            && Objects.equals( getResponseMessage(), that.getResponseMessage() )
            && Objects.equals( getStatusType(), that.getStatusType() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getResponseMessage(), getStatusType(), getStatusCode() );
    }
}
