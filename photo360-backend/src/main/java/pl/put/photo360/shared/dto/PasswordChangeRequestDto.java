package pl.put.photo360.shared.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Password change request dto, which contains all required data to change the password.
 */
@Getter
@Setter
@NoArgsConstructor
public class PasswordChangeRequestDto
{
    private String email;
    private String oldPassword;
    private String newPassword;
}
