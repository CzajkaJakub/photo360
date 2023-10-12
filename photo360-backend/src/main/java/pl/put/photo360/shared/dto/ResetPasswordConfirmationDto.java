package pl.put.photo360.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordConfirmationDto
{
    private String email;
    private String newPassword;
    private String resetPasswordToken;
}
