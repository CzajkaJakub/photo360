package pl.put.photo360;

import java.util.Objects;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Photo360client extends Application
{

    private ConfigurableApplicationContext context;

    @Override
    public void stop() throws Exception
    {
        this.context.close();
        Platform.exit();
    }

    @Override
    public void init() throws Exception
    {
        ApplicationContextInitializer< GenericApplicationContext > initializer = ac -> {
            ac.registerBean( Application.class, () -> Photo360client.this );
            ac.registerBean( Parameters.class, this::getParameters );
            ac.registerBean( HostServices.class, this::getHostServices );
        };

        this.context = new SpringApplicationBuilder().sources( Photo360JavaFxApplication.class )
            .initializers( initializer )
            .run( getParameters().getRaw()
                .toArray( new String[ 0 ] ) );
    }

    @Override
    public void start( Stage stage )
    {
        try
        {

            FXMLLoader fxmlLoader =
                new FXMLLoader( Photo360client.class.getResource( "scenes/sceneRejestracja.fxml" ) );
            fxmlLoader.setControllerFactory( context::getBean );
            Parent root = fxmlLoader.load();
            Scene mainScene = new Scene( root );

            // icon
            Image icon =
                new Image( Objects.requireNonNull( Photo360client.class.getResource( "images/icon.jpg" ) )
                    .toString() );
            stage.getIcons()
                .add( icon );

            // title
            stage.setTitle( "360 App" );
            stage.setScene( mainScene );

            stage.setResizable( false );
            stage.show();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

    }

}