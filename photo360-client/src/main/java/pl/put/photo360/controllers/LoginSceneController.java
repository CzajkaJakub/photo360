package pl.put.photo360.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.photo360.dto.LoginRequestDto;
import pl.put.photo360.dto.LoginResponseDto;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;
import java.io.IOException;

@Component
public class LoginSceneController extends SwitchSceneController {
    @FXML
    private PasswordField loginPassFX;
    @FXML
    private TextField loginLogFX;

    @Autowired
    public LoginSceneController(RequestService requestService, AuthHandler authHandler) {
        super(requestService, authHandler);
    }

    public void login( ActionEvent event )
    {
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                loginLogFX.getText(), loginPassFX.getText());

        try {
            LoginResponseDto loginResponseDto = requestService.loginUser( loginRequestDto );
            authHandler.fillWithUserData(loginResponseDto, loginRequestDto.getLogin());
        }
        catch( Exception e ) {
            System.out.println(e);
        }

        Platform.runLater( () -> {
            try {
                switchToProgramScene(event);
            }
            catch( IOException e ) {
                throw new RuntimeException( e );
            }
        } );
    }
}
