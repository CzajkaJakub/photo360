package pl.put.photo360.handlers;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pl.put.photo360.dto.LoginResponseDto;

import java.time.Instant;
import java.util.Set;

@Component
@NoArgsConstructor
public class AuthHandler {
    private String login;
    private String email;
    private String _token;
    private Boolean emailVerified;
    private Instant _tokenExpirationDate;
    private Instant _lastLoggedDatetime;
    private Set< String > userRolesList;

    public void fillWithUserData(LoginResponseDto loginResponseDto, String login) {
        this.login = login;
        this.email = loginResponseDto.getEmail();
        this._token = loginResponseDto.get_token();
        this.emailVerified = loginResponseDto.getEmailVerified();
        this._tokenExpirationDate = loginResponseDto.get_tokenExpirationDate();
        this._lastLoggedDatetime = loginResponseDto.get_lastLoggedDatetime();
        this.userRolesList = loginResponseDto.getUserRolesList();
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String get_token() {
        if (_tokenExpirationDate == null || Instant.now().isAfter(_tokenExpirationDate)) {
            return null;
        }
        return _token;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public Instant get_tokenExpirationDate() {
        return _tokenExpirationDate;
    }

    public Instant get_lastLoggedDatetime() {
        return _lastLoggedDatetime;
    }

    public Set<String> getUserRolesList() {
        return userRolesList;
    }
}
