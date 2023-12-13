package pl.put.photo360.handlers;

import java.time.Instant;
import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import pl.put.photo360.dto.LoginResponseDto;

@Component
@NoArgsConstructor
public class AuthHandler
{
    private String login;
    private String email;
    private String token;
    private Boolean emailVerified;
    private Instant tokenExpirationDate;
    private Instant lastLoggedDatetime;
    private Set< String > userRolesList;

    public void fillWithUserData( LoginResponseDto loginResponseDto, String login )
    {
        this.login = login;
        this.email = loginResponseDto.getEmail();
        this.token = loginResponseDto.get_token();
        this.emailVerified = loginResponseDto.getEmailVerified();
        this.tokenExpirationDate = loginResponseDto.get_tokenExpirationDate();
        this.lastLoggedDatetime = loginResponseDto.get_lastLoggedDatetime();
        this.userRolesList = loginResponseDto.getUserRolesList();
    }

    public void clearUserData()
    {
        this.login = null;
        this.email = null;
        this.token = null;
        this.emailVerified = null;
        this.tokenExpirationDate = null;
        this.lastLoggedDatetime = null;
        this.userRolesList = null;
    }

    public String getLogin()
    {
        return login;
    }

    public String getEmail()
    {
        return email;
    }

    public String getToken()
    {
        if( tokenExpirationDate == null )
        {
            return null;
        }
        return token;
    }

    public Boolean getEmailVerified()
    {
        return emailVerified;
    }

    public Instant getTokenExpirationDate()
    {
        return tokenExpirationDate;
    }

    public Instant getLastLoggedDatetime()
    {
        return lastLoggedDatetime;
    }

    public Set< String > getUserRolesList()
    {
        return userRolesList;
    }
}
