package pl.put.photo360.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import pl.put.photo360.ApplicationContextHolder;
import pl.put.photo360.Photo360client;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;
import java.io.IOException;

public abstract class SwitchSceneController {
    private Stage stage;
    private Scene scene;
    private FXMLLoader fxmlLoader;
    private Parent root;
    private ToolBar toolBar;
    private Label label;
    private ApplicationContext context = ApplicationContextHolder.getApplicationContext();
    protected final RequestService requestService;
    protected final AuthHandler authHandler;

    public SwitchSceneController(RequestService requestService, AuthHandler authHandler) {
        this.requestService = requestService;
        this.authHandler = authHandler;
    }

    private void setToolbarTab(ToolBar toolBar, int index) {
        Button button = (Button) toolBar.getItems().get(index);
        Platform.runLater(button::requestFocus);
    }

    public void switchScene(ActionEvent event, String scenePath) throws IOException {
        switchScene(event, scenePath, -1);
    }

    public void switchScene(ActionEvent event, String scenePath, int index) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource(scenePath));
        fxmlLoader.setControllerFactory(context::getBean);
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        if (index > -1) {
            toolBar = (ToolBar) scene.lookup("#toolbarMenu");
            setToolbarTab(toolBar, index);

            label = (Label) scene.lookup("#userLabel");
            label.setText(authHandler.getLogin());
        }

        stage.setScene(scene);
        stage.show();
    }

    public void switchToLoginScene(ActionEvent event) throws IOException {
        switchScene(event, "scenes/sceneLogowanie.fxml");
    }

    public void switchToOptionScene(ActionEvent event) throws IOException {
        switchScene(event, "scenes/sceneOpcje.fxml", 4);
    }

    public void switchToProgramScene(ActionEvent event) throws IOException {
        switchScene(event, "scenes/sceneMain.fxml", 0);
    }

    public void switchToResetPasswordScene(ActionEvent event) throws IOException {
        switchScene(event, "scenes/sceneResetPassword.fxml");
    }

    public void switchToRegisterScene(ActionEvent event) throws IOException {
        switchScene(event, "scenes/sceneRejestracja.fxml");
    }

    public void switchToScenerioScene(ActionEvent event) throws IOException {
        switchScene(event, "scenes/sceneScenariusze.fxml", 1);
    }

    public void switchToPhotosScene(ActionEvent event) throws IOException {
        switchScene(event, "scenes/scenePhotos.fxml", 2);
    }

    public void switchToInformationScene(ActionEvent event) throws IOException {
        switchScene(event, "scenes/sceneInformacje.fxml", 3);
    }
}