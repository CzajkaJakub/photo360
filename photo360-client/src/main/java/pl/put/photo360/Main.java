package pl.put.photo360;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

public class Main extends Application {
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("scenes/sceneLogowanie.fxml"));
            Parent root = fxmlLoader.load();
            Scene mainScene = new Scene(root);

            //icon
            Image icon = new Image(Objects.requireNonNull(Main.class.getResource("images/icon.jpg")).toString());
            stage.getIcons().add(icon);

            //title
            stage.setTitle("360 App");
            stage.setScene(mainScene);

            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch();
    }
}