package pl.put.photo360.shared.dto;

import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Login request dto, which contains all required data to authenticate the user.
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDto
{
    private String login;
    private String password;
    private boolean activeDirectory;

    public LoginRequestDto( String aLogin, String aPassword, boolean aActiveDirectory )
    {
        Objects.requireNonNull( aLogin );
        Objects.requireNonNull( aPassword );
        login = aLogin;
        password = aPassword;
        activeDirectory = aActiveDirectory;
    }
}
