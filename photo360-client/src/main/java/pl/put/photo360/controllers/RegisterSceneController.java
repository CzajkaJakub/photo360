package pl.put.photo360.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.put.photo360.service.RequestService;
import pl.put.photo360.web.RegisterRequest;

import java.io.IOException;

@Controller
public class RegisterSceneController extends SwitchSceneController {
    @FXML
    private TextField loginFX;
    @FXML
    private TextField emailFX;
    @FXML
    private PasswordField password1FX;
    private final RequestService requestService;
    @Autowired
    public RegisterSceneController(RequestService requestService) {
        this.requestService = requestService;
    }

    public void register(ActionEvent event) {
        RegisterRequest registerRequest = new RegisterRequest(
                loginFX.getText(),
                emailFX.getText(),
                password1FX.getText());

        try {
            Response response = requestService.registerUser(registerRequest);
            System.out.println(response.code());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> {
            try {
                switchToLoginScene(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
