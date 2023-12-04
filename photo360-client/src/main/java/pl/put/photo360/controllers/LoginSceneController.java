package pl.put.photo360.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.put.photo360.dto.LoginRequestDto;
import pl.put.photo360.dto.LoginResponseDto;
import pl.put.photo360.dto.RegisterRequestDto;
import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.service.RequestService;

import java.io.IOException;

@Controller
public class LoginSceneController extends SwitchSceneController {
    @FXML
    private PasswordField loginPassFX;
    @FXML
    private TextField loginLogFX;

    @Autowired
    public LoginSceneController(RequestService requestService) {
        super(requestService);
    }

    public void login( ActionEvent event )
    {
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                loginLogFX.getText(), loginPassFX.getText());

        try {
            LoginResponseDto loginResponseDto = requestService.loginUser( loginRequestDto );
            System.out.println( loginResponseDto.getEmail() );
            System.out.println( loginResponseDto.get_token() );
            System.out.println( loginResponseDto.getEmailVerified() );
            System.out.println( loginResponseDto.get_tokenExpirationDate() );
            System.out.println( loginResponseDto.get_lastLoggedDatetime() );
            System.out.println( loginResponseDto.getUserRolesList() );
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
