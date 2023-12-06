package pl.put.photo360.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.photo360.dto.*;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;
import pl.put.photo360.toast.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ResetPasswordSceneController extends SwitchSceneController implements Initializable {
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

    @Autowired
    public ResetPasswordSceneController(RequestService requestService, AuthHandler authHandler) {
        super(requestService, authHandler);
    }

    private void changeVisibleOfElements(boolean visible) {
        // Zmiana wyświetlania linii
        lineFX.setVisible(visible);
        lineFX.setDisable(!visible);

        // Zmiana wyświetlania sekcji wczytywania tokenu
        tokenHBox.setVisible(visible);
        tokenHBox.setDisable(!visible);

        // Zmiana wyświetlania sekcji wczytywania pass1
        pass1HBox.setVisible(visible);
        pass1HBox.setDisable(!visible);

        // Zmiana wyświetlania sekcji wczytywania pass2
        pass2HBox.setVisible(visible);
        pass2HBox.setDisable(!visible);

        // Zmiana wyświetlania przycisku resetu hasła
        resetPassButton.setVisible(visible);
        resetPassButton.setDisable(!visible);

        // Zmiana rozmiaru layoutu
        if (visible) {
            resetPassButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
            resetPassButton.setPrefHeight(Region.USE_COMPUTED_SIZE);
            resetPassHBox.setSpacing(25.0f);
        } else {
            resetPassButton.setPrefWidth(0.0f);
            resetPassButton.setPrefHeight(0.0f);
            resetPassHBox.setSpacing(0.0f);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        changeVisibleOfElements(false);
    }

    // TODO - zapisać maila po wysłaniu requesta i otrzymaniu odpowiedzi
    public void sendEmail(ActionEvent event) {

        ResetPasswordRequestDto resetPasswordRequestDto = new ResetPasswordRequestDto(
                (emailTextField.getLength() > 0 ? emailTextField.getText() : null));
        RequestResponseDto requestResponseDto = null;

        try {
            requestResponseDto = requestService.sendResetPassRequest( resetPasswordRequestDto );
        }
        catch( IOException e ) {
            Toast.showToast(event, e);
        }

        RequestResponseDto requestResponseDtoFinal = requestResponseDto;
        Platform.runLater( () -> {
            if (requestResponseDtoFinal != null) {
                changeVisibleOfElements(true);
                sendEmailButton.setText("Wyślij ponownie");
            }
        } );
    }

    // TODO - maila pobierać z zapisanego stringa, a nie bezpośrednio z pola (może być już puste)
    // TODO - naprawić działanie serwera po otrzymaniu confirmation (nie zmienia hasła)
    public void resetPass(ActionEvent event) {
        ResetPasswordConfirmationDto resetPasswordConfirmationDto = new ResetPasswordConfirmationDto(
                (emailTextField.getLength() > 0 ? emailTextField.getText() : null),
                (pass1PasswordField.getLength() > 0 ? pass1PasswordField.getText() : null),
                (tokenTextField.getLength() > 0 ? tokenTextField.getText() : null));
        RequestResponseDto requestResponseDto = null;

        try {
            requestResponseDto = requestService.confirmResetPassword( resetPasswordConfirmationDto );
        }
        catch( IOException e ) {
            Toast.showToast(event, e);
        }

        RequestResponseDto requestResponseDtoFinal = requestResponseDto;
        Platform.runLater( () -> {
            if (requestResponseDtoFinal != null) {
                Toast.showToast(event, "Hasło zostało pomyślnie zmienione");
                try {
                    switchToLoginScene(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } );
    }
}
