package pl.put.photo360.dto;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

public class AuthHandler {
    private static String email;
    private static String _token;
    private static Boolean emailVerified;
    private static Instant _tokenExpirationDate;
    private static Instant _lastLoggedDatetime;
    private static Set< String > userRolesList;

    public AuthHandler(LoginResponseDto loginResponseDto) {
        email = loginResponseDto.getEmail();
        _token = loginResponseDto.get_token();
        emailVerified = loginResponseDto.getEmailVerified();
        _tokenExpirationDate = loginResponseDto.get_tokenExpirationDate();
        _lastLoggedDatetime = loginResponseDto.get_lastLoggedDatetime();
        userRolesList = loginResponseDto.getUserRolesList();
    }

    public static String getEmail() {
        return email;
    }

    public static String get_token() {
        return _token;
    }

    public static Boolean getEmailVerified() {
        return emailVerified;
    }

    public static Instant get_tokenExpirationDate() {
        return _tokenExpirationDate;
    }

    public static Instant get_lastLoggedDatetime() {
        return _lastLoggedDatetime;
    }

    public static Set<String> getUserRolesList() {
        return userRolesList;
    }
}
