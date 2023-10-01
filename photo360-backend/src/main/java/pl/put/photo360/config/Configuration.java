package pl.put.photo360.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties( prefix = "server.properties" )
@PropertySource( value = "classpath:/application.properties", encoding = "UTF-8" )
public class Configuration
{
    private String PUBLIC_API_KEY;
    private Integer MAX_LOGIN_ATTEMPT;
    private Integer ACCOUNT_LOCK_TIME;
    private Integer TOKEN_EXPIRATION_TIME;
    private Integer RESET_PASSWORD_TOKEN_EXPIRATION;
    private Integer MIN_REGISTER_PASSWORD_LENGTH;
    private Integer MAX_REGISTER_FIELD_LENGTH;
    private String EMAIL_REGEX;
    private Integer KEY_LENGTH_BYTES;
    private String SUPPORTED_FORMAT;
    private String RESET_PASSWORD_EMAIL_SUBJECT;
    private String RESET_PASSWORD_EMAIL_TEXT;
    private List< String > SUPPORTED_PHOTO_FORMATS;
}