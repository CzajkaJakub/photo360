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
}
