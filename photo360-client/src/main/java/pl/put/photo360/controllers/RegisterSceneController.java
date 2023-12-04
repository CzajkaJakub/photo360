package pl.put.photo360.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.service.RequestService;
import pl.put.photo360.dto.RegisterRequestDto;
import java.io.IOException;

@Controller
public class RegisterSceneController extends SwitchSceneController
{
    @FXML
    private TextField loginFX;
    @FXML
    private TextField emailFX;
    @FXML
    private PasswordField password1FX;

    @Autowired
    public RegisterSceneController(RequestService requestService) {
        super(requestService);
    }

    public void register( ActionEvent event )
    {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                loginFX.getText(), emailFX.getText(), password1FX.getText());

        try {
            RequestResponseDto requestResponseDto = requestService.registerUser( registerRequestDto );
            System.out.println( requestResponseDto.getStatusCode() );
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
