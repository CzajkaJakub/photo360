package pl.put.photo360.shared.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple class which contains a message as response to frontend.
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Setter
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
}
