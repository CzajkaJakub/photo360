package pl.put.photo360.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;
import pl.put.photo360.dto.RegisterRequestDto;
import java.io.IOException;

@Component
public class RegisterSceneController extends SwitchSceneController
{
    @FXML
    private TextField loginFX;
    @FXML
    private TextField emailFX;
    @FXML
    private PasswordField password1FX;

    @Autowired
    public RegisterSceneController(RequestService requestService, AuthHandler authHandler) {
        super(requestService, authHandler);
    }

    public void register( ActionEvent event )
    {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                loginFX.getText(), emailFX.getText(), password1FX.getText());

        try {
            RequestResponseDto requestResponseDto = requestService.registerUser( registerRequestDto );
        }
        catch( Exception e ) {
            System.out.println(e);
        }

        Platform.runLater( () -> {
            try {
                switchToLoginScene( event );
            }
            catch( IOException e ) {
                throw new RuntimeException( e );
            }
        } );
    }
}
