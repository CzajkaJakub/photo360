package pl.put.photo360.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dto.RegisterRequestDto;
import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.dto.ToastsConstants;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;
import pl.put.photo360.toast.Toast;

@Component
public class RegisterSceneController extends SwitchSceneController
{
    @FXML
    private TextField loginField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField password1Field;
    @FXML
    private PasswordField password2Field;

    @Autowired
    public RegisterSceneController( RequestService requestService, AuthHandler authHandler,
        Configuration configuration )
    {
        super( requestService, authHandler, configuration );
    }

    public void register( ActionEvent event )
    {
        // Sprawdzanie czy hasła są identyczne
        if( !password1Field.getText()
            .equals( password2Field.getText() ) )
        {
            Toast.showToast( event, ToastsConstants.NOT_THE_SAME_PASSWORDS.getPath() );
            return;
        }

        RegisterRequestDto registerRequestDto =
            requestService.createRequest( RegisterRequestDto.class, loginField, emailField, password1Field );

        RequestResponseDto requestResponseDto = requestService.executeRequest( event, registerRequestDto,
            configuration.getREGISTER_ENDPOINT_URL(), RequestResponseDto.class );

        Platform.runLater( () -> {
            if( requestResponseDto != null )
            {
                try
                {
                    switchToLoginScene( event );
                }
                catch( IOException e )
                {
                    throw new RuntimeException( e );
                }
            }
        } );
    }
}
