package pl.put.photo360.toast;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Toast {

    public static void showToast(ActionEvent actionEvent, IOException exception) {
        String message = exception.getCause().toString().split(":")[1];
        Node source = (Node) actionEvent.getSource();
        Stage ownerStage = (Stage) source.getScene().getWindow();

        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        Label label = new Label(message);
        label.setFont(new Font("Arial", 16));
        label.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 10px;");

        StackPane content = new StackPane(label);
        content.setStyle("-fx-background-radius: 10; -fx-background-color: rgba(0, 0, 0, 0.75);");
        popup.getContent().add(content);

        popup.setOnShown(event -> {
            popup.setX(ownerStage.getX() + (ownerStage.getWidth() / 2) - (popup.getWidth() / 2));
            popup.setY(ownerStage.getY() + (ownerStage.getHeight() / 1.1) - (popup.getHeight() / 2));
        });

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> popup.hide());
        delay.play();

        popup.show(ownerStage);
    }
}

