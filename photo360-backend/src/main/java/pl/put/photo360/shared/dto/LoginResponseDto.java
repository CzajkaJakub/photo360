package pl.put.photo360.shared.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto which contains authentication data if auth passed correctly.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto
{
    private String email;
    private String _token;
    private Instant _tokenExpirationDate;
    private Instant _lastLoggedDatetime;
}
