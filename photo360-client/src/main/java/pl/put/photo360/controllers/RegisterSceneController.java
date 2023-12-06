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
    @FXML
    private PasswordField password2FX;

    @Autowired
    public RegisterSceneController(RequestService requestService, AuthHandler authHandler) {
        super(requestService, authHandler);
    }

    public void register( ActionEvent event )
    {
        System.out.println(password1FX.getText());
        System.out.println(password2FX.getText());

        // Sprawdzanie czy hasła są identyczne
        if (!password1FX.getText().equals(password2FX.getText())) {
            System.out.println("Hasła nie są takie same!");
            return;
        }

        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                (loginFX.getLength() > 0 ? loginFX.getText() : null) ,
                (emailFX.getLength() > 0 ? emailFX.getText() : null),
                (password1FX.getLength() > 0 ? password1FX.getText() : null));
        RequestResponseDto requestResponseDto = null;

        try {
            requestResponseDto = requestService.registerUser( registerRequestDto );
        }
        catch( IOException e ) {
            System.out.println("Kontroller - dostałem wyjątek: " + e.getMessage());
        }

        RequestResponseDto requestResponseDtoFinal = requestResponseDto;
        Platform.runLater( () -> {
            if (requestResponseDtoFinal != null) {
                try {
                    switchToLoginScene(event);
                } catch (IOException e) {
                    throw new RuntimeException( e );
                }
            }
        } );
    }
}
