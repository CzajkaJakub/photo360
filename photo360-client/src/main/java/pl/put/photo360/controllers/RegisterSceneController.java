package pl.put.photo360.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import okhttp3.*;
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
//            ObjectMapper objectMapper = new ObjectMapper();
//            String json = objectMapper.writeValueAsString(registerRequest);
//            RequestBody formBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
//
//            Request request = new Request.Builder()
//                    .url("http://127.0.0.1:8095/photo360/authorization/register")
//                    .addHeader("publicApiKey", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJpt3Bs6fwMc2S7h5cpIP6nkG9DsISp0MfKTpwtt31/a1ZF2+Pv8I0f64CIcBj4GPWP4PWWe9nI4WSUKkf5CdxT6sUh4toHvBemfQiSw3sCaHfgL0WBrdqhqIxYUwsedb9ZuCXRp6acmbvqttNI2r5V8rsuT0nTDYCnVTl5OgnQIDAQAB")
//                    .post(formBody)
//                    .build();
//
//            OkHttpClient client = new OkHttpClient();
//            Response response = client.newCall(request).execute();


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
