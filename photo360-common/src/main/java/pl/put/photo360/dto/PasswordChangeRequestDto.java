package pl.put.photo360.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Password change request dto, which contains all required data to change the password.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequestDto
{
    private String oldPassword;
    private String newPassword;
}
