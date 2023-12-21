package pl.put.photo360.controllers;

import java.io.IOException;

import org.springframework.context.ApplicationContext;

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
import pl.put.photo360.ApplicationContextHolder;
import pl.put.photo360.Photo360client;
import pl.put.photo360.camera.view.CameraWindow;
import pl.put.photo360.config.ConfigURL;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dto.ResourcesConstants;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;

public abstract class SwitchSceneController
{
    protected final RequestService requestService;
    protected final AuthHandler authHandler;
    protected final Configuration configuration;
    protected final CameraWindow cameraWindow;
    protected final ConfigURL configURL;
    private Stage stage;
    private Scene scene;
    private FXMLLoader fxmlLoader;
    private Parent root;
    private ToolBar toolBar;
    private Label label;
    private ApplicationContext context = ApplicationContextHolder.getApplicationContext();

    public SwitchSceneController(RequestService requestService, AuthHandler authHandler,
                                 Configuration configuration, ConfigURL configURL, CameraWindow cameraWindow)
    {
        this.requestService = requestService;
        this.authHandler = authHandler;
        this.configuration = configuration;
        this.configURL = configURL;
        this.cameraWindow = cameraWindow;
    }

    private void setToolbarTab( ToolBar toolBar, int index )
    {
        Button button = (Button)toolBar.getItems()
            .get( index );
        Platform.runLater( button::requestFocus );
    }

    public void switchScene( ActionEvent event, String scenePath ) throws IOException
    {
        switchScene( event, scenePath, -1 );
    }

    public void switchScene( ActionEvent event, String scenePath, int index ) throws IOException
    {
        fxmlLoader = new FXMLLoader( Photo360client.class.getResource( scenePath ) );
        fxmlLoader.setControllerFactory( context::getBean );
        root = fxmlLoader.load();

        stage = (Stage)((Node)event.getSource()).getScene()
            .getWindow();
        scene = new Scene( root );

        if( index > -1 )
        {
            toolBar = (ToolBar)scene.lookup( "#toolbarMenu" );
            setToolbarTab( toolBar, index );

            label = (Label)scene.lookup( "#userLabel" );
            label.setText( authHandler.getLogin() );
        }

        stage.setScene( scene );
        stage.show();
    }

    public void switchToLoginScene( ActionEvent event ) throws IOException
    {
        switchScene( event, ResourcesConstants.SCENE_LOGIN.getPath() );
    }

    public void switchToOptionScene( ActionEvent event ) throws IOException
    {
        switchScene( event, ResourcesConstants.SCENE_OPTIONS.getPath(), 3 );
    }

    public void switchToProgramScene( ActionEvent event ) throws IOException
    {
        switchScene( event, ResourcesConstants.SCENE_MAIN.getPath(), 0 );
    }

    public void switchToResetPasswordScene( ActionEvent event ) throws IOException
    {
        switchScene( event, ResourcesConstants.SCENE_RESET_PASSWORD.getPath() );
    }

    public void switchToRegisterScene( ActionEvent event ) throws IOException
    {
        switchScene( event, ResourcesConstants.SCENE_REGISTER.getPath() );
    }

    public void switchToPhotosScene( ActionEvent event ) throws IOException
    {
        switchScene( event, ResourcesConstants.SCENE_PHOTOS.getPath(), 1 );
    }

    public void switchToInformationScene( ActionEvent event ) throws IOException
    {
        switchScene( event, ResourcesConstants.SCENE_INFORMATIONS.getPath(), 2 );
    }

    public void logout( ActionEvent event ) throws IOException
    {
        authHandler.clearUserData();
        switchToLoginScene( event );
    }
}