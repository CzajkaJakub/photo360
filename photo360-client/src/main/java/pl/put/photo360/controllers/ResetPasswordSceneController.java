package pl.put.photo360.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dto.LabelsConstants;
import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.dto.ResetPasswordConfirmationDto;
import pl.put.photo360.dto.ResetPasswordRequestDto;
import pl.put.photo360.dto.ToastsConstants;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;
import pl.put.photo360.toast.Toast;

@Component
public class ResetPasswordSceneController extends SwitchSceneController implements Initializable
{
    @FXML
    private TextField emailTextField;
    @FXML
    private Button sendEmailButton;
    @FXML
    private Line lineFX;
    @FXML
    private HBox tokenHBox;
    @FXML
    private TextField tokenTextField;
    @FXML
    private HBox pass1HBox;
    @FXML
    private PasswordField pass1PasswordField;
    @FXML
    private HBox pass2HBox;
    @FXML
    private PasswordField pass2PasswordField;
    @FXML
    private HBox resetPassHBox;
    @FXML
    private Button resetPassButton;
    @FXML
    private Button backButton;
    private String emailForConfirm = null;

    @Autowired
    public ResetPasswordSceneController( RequestService requestService, AuthHandler authHandler,
        Configuration configuration )
    {
        super( requestService, authHandler, configuration );
    }

    private void changeVisibleOfElements( boolean visible )
    {
        // Zmiana wyświetlania linii
        lineFX.setVisible( visible );
        lineFX.setDisable( !visible );

        // Zmiana wyświetlania sekcji wczytywania tokenu
        tokenHBox.setVisible( visible );
        tokenHBox.setDisable( !visible );

        // Zmiana wyświetlania sekcji wczytywania pass1
        pass1HBox.setVisible( visible );
        pass1HBox.setDisable( !visible );

        // Zmiana wyświetlania sekcji wczytywania pass2
        pass2HBox.setVisible( visible );
        pass2HBox.setDisable( !visible );

        // Zmiana wyświetlania przycisku resetu hasła
        resetPassButton.setVisible( visible );
        resetPassButton.setDisable( !visible );

        // Zmiana rozmiaru layoutu
        if( visible )
        {
            resetPassButton.setPrefWidth( Region.USE_COMPUTED_SIZE );
            resetPassButton.setPrefHeight( Region.USE_COMPUTED_SIZE );
            resetPassHBox.setSpacing( 25.0f );
        }
        else
        {
            resetPassButton.setPrefWidth( 0.0f );
            resetPassButton.setPrefHeight( 0.0f );
            resetPassHBox.setSpacing( 0.0f );
        }

    }

    @Override
    public void initialize( URL url, ResourceBundle resourceBundle )
    {
        changeVisibleOfElements( false );
    }

    public void sendEmail( ActionEvent event )
    {
        ResetPasswordRequestDto resetPasswordRequestDto =
            requestService.createRequest( ResetPasswordRequestDto.class, emailTextField );

        RequestResponseDto requestResponseDto = requestService.executeRequest( event, resetPasswordRequestDto,
            configuration.getREQUEST_RESET_PASSWORD(), RequestResponseDto.class );

        Platform.runLater( () -> {
            if( requestResponseDto != null )
            {
                emailForConfirm = emailTextField.getText();
                changeVisibleOfElements( true );
                sendEmailButton.setText( LabelsConstants.SEND_AGAIN.getPath() );
            }
        } );
    }

    // TODO - naprawić działanie serwera po otrzymaniu confirmation (nie zmienia hasła)
    public void resetPass( ActionEvent event )
    {
        // Sprawdzanie czy hasła są identyczne
        if( !pass1PasswordField.getText()
            .equals( pass2PasswordField.getText() ) )
        {
            Toast.showToast( event, ToastsConstants.NOT_THE_SAME_PASSWORDS.getPath() );
            return;
        }

        ResetPasswordConfirmationDto resetPasswordConfirmationDto = requestService.createRequest(
            ResetPasswordConfirmationDto.class, emailTextField, pass1PasswordField, tokenTextField );

        RequestResponseDto requestResponseDto =
            requestService.executeRequest( event, resetPasswordConfirmationDto,
                configuration.getCONFIRMATION_RESET_PASSWORD(), RequestResponseDto.class );

        Platform.runLater( () -> {
            if( requestResponseDto != null )
            {
                Toast.showToast( event, ToastsConstants.PASSWORD_CHANGED.getPath() );
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
