package pl.put.photo360.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties( prefix = "server.properties" )
public class Configuration
{
    private String PUBLIC_API_KEY;
    private Integer MAX_LOGIN_ATTEMPT;
    private Integer ACCOUNT_LOCK_TIME;
    private Integer TOKEN_EXPIRATION_TIME;
}