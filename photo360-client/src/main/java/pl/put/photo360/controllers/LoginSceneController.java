package pl.put.photo360.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dto.LoginRequestDto;
import pl.put.photo360.dto.LoginResponseDto;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;
import pl.put.photo360.toast.Toast;

import java.io.IOException;

@Component
public class LoginSceneController extends SwitchSceneController {
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField loginField;

    @Autowired
    public LoginSceneController(RequestService requestService, AuthHandler authHandler, Configuration configuration) {
        super(requestService, authHandler, configuration);
    }

    public void login( ActionEvent event )
    {
        LoginRequestDto loginRequestDto = requestService.createRequest(LoginRequestDto.class, loginField, passwordField);

        LoginResponseDto loginResponseDto = requestService.executeRequest(event, loginRequestDto,
                configuration.getLOGIN_ENDPOINT_URL(), LoginResponseDto.class);

        Platform.runLater( () -> {
            if (loginResponseDto != null) {
                try {
                    authHandler.fillWithUserData(loginResponseDto, loginRequestDto.getLogin());
                    switchToProgramScene(event);
                }
                catch( IOException e ) {
                    throw new RuntimeException( e );
                }
            }
        } );
    }
}
