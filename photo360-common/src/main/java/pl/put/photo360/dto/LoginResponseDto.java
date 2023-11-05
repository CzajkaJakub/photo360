package pl.put.photo360.dto;

import java.time.Instant;
import java.util.Set;

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
    private Boolean emailVerified;
    private Instant _tokenExpirationDate;
    private Instant _lastLoggedDatetime;
    private Set< String > userRolesList;
}
