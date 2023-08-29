package pl.put.photo360.shared.dto;

import java.time.Instant;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto which contains authentication data if auth passed correctly.
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDto
{
    private String email;
    private String _token;
    private Instant _tokenExpirationDate;
    private Instant _lastLoggedDatetime;

    public LoginResponseDto( String aEmail, String aToken, Instant aTokenExpirationDate,
        Instant aLastLoggedDatetime )
    {
        Objects.requireNonNull( aEmail );
        Objects.requireNonNull( aToken );
        Objects.requireNonNull( aTokenExpirationDate );
        email = aEmail;
        _token = aToken;
        _tokenExpirationDate = aTokenExpirationDate;
        _lastLoggedDatetime = aLastLoggedDatetime;
    }
}
