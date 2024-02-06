package pl.put.photo360.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import pl.put.photo360.camera.view.CameraWindow;
import pl.put.photo360.config.ConfigURL;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dto.LabelsConstants;
import pl.put.photo360.dto.PasswordChangeRequestDto;
import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.dto.ToastsConstants;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;
import pl.put.photo360.toast.Toast;

@Component
public class OptionsSceneController extends SwitchSceneController implements Initializable
{
    @FXML
    private Label verifyLabel;

    @FXML
    private Button verifyButton;

    @FXML
    private HBox confirmHBox;

    @FXML
    private TextField confirmTextField;

    @FXML
    private Button confirmButton;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField repeatNewPasswordField;

    @FXML
    private Button changeButton;

    @Autowired
    public OptionsSceneController( RequestService requestService, AuthHandler authHandler,
        Configuration configuration, ConfigURL configURL, CameraWindow cameraWindow )
    {
        super( requestService, authHandler, configuration, configURL, cameraWindow );
    }

    private void changeEmailVerifyVisibility()
    {
        verifyButton.setVisible( false );
        verifyButton.setDisable( true );
        verifyButton.setPrefHeight( 0.0 );
        verifyButton.setPrefWidth( 0.0 );

        verifyLabel.setText( LabelsConstants.VERIFIED_LABEL.getLabel() );
    }

    private void changeConfirmCodeVisibility( boolean isVisible )
    {
        confirmHBox.setVisible( isVisible );
        confirmHBox.setDisable( !isVisible );

        if( isVisible )
        {
            confirmHBox.setPrefHeight( Region.USE_COMPUTED_SIZE );
        }
        else
        {
            confirmHBox.setPrefHeight( 0.0 );
        }
    }

    @Override
    public void initialize( URL url, ResourceBundle resourceBundle )
    {
        changeConfirmCodeVisibility( false );
        if( authHandler.getEmailVerified() )
        {
            changeEmailVerifyVisibility();
        }
    }

    public void requestConfirmation( ActionEvent event )
    {
        RequestResponseDto requestResponseDto = requestService.executeGetRequest( event,
            configURL.getREQUEST_VERIFY_EMAIL_URL(), RequestResponseDto.class );

        Platform.runLater( () -> {
            if( requestResponseDto != null )
            {
                changeConfirmCodeVisibility( true );
                verifyButton.setText( LabelsConstants.SEND_AGAIN.getLabel() );
            }
        } );
    }

    public void confirmVerifyEmail( ActionEvent event )
    {
        if( confirmTextField.getText()
            .isBlank() )
        {
            Toast.showToast( event, ToastsConstants.EMPTY_CONFIRMATION_CODE.getMessage() );
            return;
        }
        String url = configURL.getCONFIRM_VERIFY_EMAIL_URL() + "/" + confirmTextField.getText();
        RequestResponseDto requestResponseDto =
            requestService.executeGetRequest( event, url, RequestResponseDto.class );

        Platform.runLater( () -> {
            if( requestResponseDto != null )
            {
                try
                {
                    Toast.showToast( event, ToastsConstants.EMAIL_VERIFIED.getMessage() );
                    logout( event );
                }
                catch( IOException e )
                {
                    throw new RuntimeException( e );
                }
            }
        } );
    }

    public void changePassword( ActionEvent event )
    {
        // Sprawdzanie czy hasła są identyczne
        if( !newPasswordField.getText()
            .equals( repeatNewPasswordField.getText() ) )
        {
            Toast.showToast( event, ToastsConstants.NOT_THE_SAME_PASSWORDS.getMessage() );
            return;
        }

        PasswordChangeRequestDto passwordChangeRequestDto = requestService
            .createRequest( PasswordChangeRequestDto.class, oldPasswordField, newPasswordField );

        RequestResponseDto requestResponseDto =
            requestService.executeRequest( event, passwordChangeRequestDto,
                configURL.getCHANGE_PASSWORD_URL(), RequestResponseDto.class, HttpMethod.PUT );

        Platform.runLater( () -> {
            if( requestResponseDto != null )
            {
                Toast.showToast( event, ToastsConstants.PASSWORD_CHANGED.getMessage() );
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
