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
//    private final SpringFXMLLoader fxmlLoader;
//
//    public Main(SpringFXMLLoader fxmlLoader) {
//        this.fxmlLoader = fxmlLoader;
//    }

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
//            Parent root = fxmlLoader.load("scenes/sceneRejestracja.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("scenes/sceneLogowanie.fxml"));
            System.out.println(fxmlLoader.getResources());
//            fxmlLoader = new FXMLLoader(Main.class.getResource("scenes/sceneMain.fxml"));
            System.out.println(fxmlLoader.getResources());
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