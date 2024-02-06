package pl.put.photo360.camera.view;

import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.core.Mat;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.put.photo360.camera.controller.CameraController;
import pl.put.photo360.camera.utils.Utils;
import pl.put.photo360.dto.LabelsConstants;

public class CameraWindow
{
    private CameraController cameraController;
    private ImageView imageView;
    private Stage cameraStage;
    private Timer timer;

    public CameraWindow()
    {
//        this.cameraController = new CameraController();
//        this.imageView = new ImageView();
//        this.timer = new Timer();
    }

    public void startCamera( Stage primaryStage )
    {
        if( cameraStage != null && cameraStage.isShowing() )
        {
            return;
        }

        cameraStage = new Stage();
        cameraStage.setTitle( LabelsConstants.CAMERA_STAGE_TITLE.getLabel() );
        cameraStage.initModality( Modality.WINDOW_MODAL );
        cameraStage.initOwner( primaryStage );
        cameraStage.setResizable( false );

        StackPane root = new StackPane();
        root.getChildren()
            .add( imageView );
        Scene scene = new Scene( root, 600, 400 );
        cameraStage.setScene( scene );

        timer.schedule( new TimerTask()
        {
            @Override
            public void run()
            {
                Mat frame = cameraController.startCamera();
                BufferedImage imageToShow = Utils.matToBufferedImage( frame );
                Platform
                    .runLater( () -> { imageView.setImage( SwingFXUtils.toFXImage( imageToShow, null ) ); } );
            }
        }, 0, 33 ); // ~30 klatek na sekundę

        cameraStage.setOnCloseRequest( event -> closeWindow() );
        cameraStage.showAndWait();
    }

    public void closeWindow()
    {
        // Zatrzymuje timer
        if( timer != null )
        {
            timer.cancel();
            timer = new Timer();
        }

        // Zwalnia zasoby kamery
        if( cameraController != null )
        {
            cameraController.stopCamera();
        }

        // Zamyka Stage, jeśli jest otwarty
        if( cameraStage != null && cameraStage.isShowing() )
        {
            cameraStage.close();
        }
    }

}
