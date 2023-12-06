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
import pl.put.photo360.toast.Toast;

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
                (loginLogFX.getLength() > 0 ? loginLogFX.getText() : null),
                (loginPassFX.getLength() > 0 ? loginPassFX.getText() : null));
        LoginResponseDto loginResponseDto = null;

        try {
            loginResponseDto = requestService.loginUser( loginRequestDto );
            authHandler.fillWithUserData(loginResponseDto, loginRequestDto.getLogin());
        }
        catch( IOException e ) {
            Toast.showToast(event, e);
        }

        LoginResponseDto loginResponseDtoFinal = loginResponseDto;
        Platform.runLater( () -> {
            if (loginResponseDtoFinal != null) {
                try {
                    switchToProgramScene(event);
                }
                catch( IOException e ) {
                    throw new RuntimeException( e );
                }
            }
        } );
    }
}
