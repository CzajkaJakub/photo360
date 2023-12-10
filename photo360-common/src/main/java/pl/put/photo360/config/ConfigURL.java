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
@ConfigurationProperties( prefix = "server.url" )
@PropertySource( value = "classpath:/application.properties", encoding = "UTF-8" )
public class ConfigURL {
    private String LOGIN_ENDPOINT_URL;
    private String REGISTER_ENDPOINT_URL;
    private String REQUEST_VERIFY_EMAIL_URL;
    private String CONFIRM_VERIFY_EMAIL_URL;
    private String CHANGE_PASSWORD_URL;
    private String REQUEST_RESET_PASSWORD;
    private String CONFIRMATION_RESET_PASSWORD;
    private String UPLOAD_PHOTOS_URL;
}
