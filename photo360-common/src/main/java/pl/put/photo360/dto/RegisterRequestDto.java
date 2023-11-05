package pl.put.photo360.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Register request dto, which contains all required data to register.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto
{
    private String login;
    private String email;
    private String password;
}
