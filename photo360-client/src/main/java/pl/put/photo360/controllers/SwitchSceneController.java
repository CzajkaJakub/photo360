package pl.put.photo360.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Controller;
import pl.put.photo360.ApplicationContextHolder;
import pl.put.photo360.Photo360JavaFxApplication;
import pl.put.photo360.Photo360client;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.service.RequestService;

import javax.tools.Tool;
import java.io.IOException;

@Controller
public class SwitchSceneController {
    private Stage stage;
    private Scene scene;
    private FXMLLoader fxmlLoader;
    private Parent root;
    private ToolBar toolBar;
    private ApplicationContext context = ApplicationContextHolder.getApplicationContext();
    public final RequestService requestService;

    @Autowired
    public SwitchSceneController(RequestService requestService) {
        this.requestService = requestService;
    }

    private void setToolbarTab(ToolBar toolBar, int index) {
        Button button = (Button) toolBar.getItems().get(index);
        Platform.runLater(button::requestFocus);
    }

    public void switchToLoginScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneLogowanie.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public void switchToOptionScene(ActionEvent event) throws IOException {

        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneOpcje.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        root = fxmlLoader.load();


        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        toolBar = (ToolBar) scene.lookup("#toolbarMenu");
        setToolbarTab(toolBar, 4);

        stage.setScene(scene);
        stage.show();


    }

    public void switchToProgramScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneMain.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        toolBar = (ToolBar) scene.lookup("#toolbarMenu");
        setToolbarTab(toolBar, 0);

        stage.setScene(scene);
        stage.show();
    }

    public void switchToResetPasswordScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneResetPassword.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public void switchToRegisterScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneRejestracja.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public void switchToScenerioScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneScenariusze.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        toolBar = (ToolBar) scene.lookup("#toolbarMenu");
        setToolbarTab(toolBar, 1);

        stage.setScene(scene);
        stage.show();
    }

    public void switchToPhotosScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/scenePhotos.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        toolBar = (ToolBar) scene.lookup("#toolbarMenu");
        setToolbarTab(toolBar, 2);

        stage.setScene(scene);
        stage.show();
    }

    public void switchToInformationScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneInformacje.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        toolBar = (ToolBar) scene.lookup("#toolbarMenu");
        setToolbarTab(toolBar, 3);

        stage.setScene(scene);
        stage.show();
    }
}