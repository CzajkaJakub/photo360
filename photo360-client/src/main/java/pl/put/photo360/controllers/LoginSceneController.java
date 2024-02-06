package pl.put.photo360.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pl.put.photo360.camera.view.CameraWindow;
import pl.put.photo360.config.ConfigURL;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dto.LoginRequestDto;
import pl.put.photo360.dto.LoginResponseDto;
import pl.put.photo360.dto.ToastsConstants;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;
import pl.put.photo360.toast.Toast;

@Component
public class LoginSceneController extends SwitchSceneController
{
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField loginField;

    @Autowired
    public LoginSceneController( RequestService requestService, AuthHandler authHandler,
        Configuration configuration, ConfigURL configURL, CameraWindow cameraWindow )
    {
        super( requestService, authHandler, configuration, configURL, cameraWindow );
    }

    public void login( ActionEvent event )
    {
        LoginRequestDto loginRequestDto =
            requestService.createRequest( LoginRequestDto.class, loginField, passwordField );

        LoginResponseDto loginResponseDto = requestService.executeRequest( event, loginRequestDto,
            configURL.getLOGIN_ENDPOINT_URL(), LoginResponseDto.class );

        Platform.runLater( () -> {
            if( loginResponseDto != null )
            {
                try
                {
                    authHandler.fillWithUserData( loginResponseDto, loginRequestDto.getLogin() );
                    if( !loginResponseDto.getEmailVerified() )
                    {
                        Toast.showToast( event, ToastsConstants.PLEASE_VERIFY_EMAIL.getMessage() );
                    }
                    switchToProgramScene( event );
                }
                catch( IOException e )
                {
                    throw new RuntimeException( e );
                }
            }
        } );
    }
}
