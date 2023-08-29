package pl.put.photo360.shared.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Register request dto, which contains all required data to register.
 */
@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestDto
{
    private String login;
    private String email;
    private String password;

    public RegisterRequestDto( String login, String email, String password )
    {
        this.login = login;
        this.email = email;
        this.password = password;
    }
}
