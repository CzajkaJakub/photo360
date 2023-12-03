package pl.put.photo360.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;
import pl.put.photo360.Photo360client;

import java.io.IOException;

@Controller
public class SwitchSceneController {
    private Stage stage;
    private Scene scene;
    private FXMLLoader fxmlLoader;
    private Parent root;

    private void setToolbarTab(int index) {
        ToolBar toolbar = (ToolBar) scene.lookup("#toolbarMenu");
        Node buttonToFocus = toolbar.getItems().get(index);
        Platform.runLater(buttonToFocus::requestFocus);
    }

    public void switchToLoginScene(ActionEvent event) throws IOException {
//        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneLogowanie.fxml"));
//        root = fxmlLoader.load();
//
//        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//
//        //String css = this.getClass().getResource("/application.css").toExternalForm();
//        //scene.getStylesheets().add(css);
//
//        stage.setScene(scene);
//        stage.show();
    }

    public void switchToOptionScene(ActionEvent event) throws IOException {

        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneOpcje.fxml"));
        root = fxmlLoader.load();


        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        setToolbarTab(4);
        //String css = this.getClass().getResource("/application.css").toExternalForm();
        //scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.show();


    }

    public void switchToProgramScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneMain.fxml"));
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        //String css = this.getClass().getResource("/application.css").toExternalForm();
        //scene.getStylesheets().add(css);

        setToolbarTab(0);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToResetPasswordScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneResetPassword.fxml"));
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        //String css = this.getClass().getResource("/application.css").toExternalForm();
        //scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.show();
    }

    public void switchToRegisterScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneRejestracja.fxml"));
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        //String css = this.getClass().getResource("/application.css").toExternalForm();
        //scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.show();
    }

    public void switchToScenerioScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneScenariusze.fxml"));
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        //String css = this.getClass().getResource("/application.css").toExternalForm();
        //scene.getStylesheets().add(css);

        setToolbarTab(1);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToPhotosScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/scenePhotos.fxml"));
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        //String css = this.getClass().getResource("/application.css").toExternalForm();
        //scene.getStylesheets().add(css);

        setToolbarTab(2);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToInformationScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(Photo360client.class.getResource("scenes/sceneInformacje.fxml"));
        root = fxmlLoader.load();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        //String css = this.getClass().getResource("/application.css").toExternalForm();
        //scene.getStylesheets().add(css);


        setToolbarTab(3);
        stage.setScene(scene);
        stage.show();
    }
}