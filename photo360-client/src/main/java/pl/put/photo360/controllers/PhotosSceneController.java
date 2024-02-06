package pl.put.photo360.controllers;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import pl.put.photo360.camera.view.CameraWindow;
import pl.put.photo360.config.ConfigURL;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dto.PhotoDataDto;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;

@Component
public class PhotosSceneController extends SwitchSceneController implements Initializable
{
    @FXML
    private GridPane previewGrid;

    @FXML
    private Label setTitleLabel;

    @FXML
    private ImageView mainImageView;

    @FXML
    private Button leftButton;

    @FXML
    private Button rightButton;

    @FXML
    private ScrollPane miniScrollPane;

    @FXML
    private HBox miniHBox;

    private List< PhotoDataDto > photoDataDtoList;

    private List< Image > images;

    @Autowired
    public PhotosSceneController( RequestService requestService, AuthHandler authHandler,
        Configuration configuration, ConfigURL configURL, CameraWindow cameraWindow )
    {
        super( requestService, authHandler, configuration, configURL, cameraWindow );
    }

    @Override
    public void initialize( URL url, ResourceBundle resourceBundle )
    {
        images = new ArrayList<>();

        URI uri = UriComponentsBuilder.fromHttpUrl( configURL.getDOWNLOAD_PRIVATE_GIFS_URL() )
            .queryParam( "previewMode", true )
            .build()
            .toUri();

        ParameterizedTypeReference< List< PhotoDataDto > > typeRef =
            new ParameterizedTypeReference< List< PhotoDataDto > >()
            {};
        photoDataDtoList =
            requestService.executeRequest( new ActionEvent(), null, uri, typeRef, HttpMethod.GET );

        int rowID = 0;
        int columnID = 0;
        Collections.reverse( photoDataDtoList );
        for( var photoDataDto : photoDataDtoList )
        {
            addPhotoToPreviewGrid( photoDataDto, columnID, rowID );
            columnID += 1;
            if( columnID > 1 )
            {
                rowID += 1;
                columnID = 0;
            }
        }

        // if (!photoDataDtoList.isEmpty()) {
        // checkVisibleSet(null, photoDataDtoList.get(0));
        // }
    }

    private void addPhotoToPreviewGrid( PhotoDataDto photoDataDto, int columnID, int rowID )
    {
        ByteArrayInputStream inputStream = new ByteArrayInputStream( photoDataDto.getHeadPhoto() );
        Image image = new Image( inputStream );
        String title = photoDataDto.getTitle()
            .replace( "\"", "" );

        VBox vBox = new VBox();
        vBox.setLayoutX( 10.0 );
        vBox.setLayoutY( 10.0 );
        vBox.setPrefSize( 90.0, 80.0 );
        vBox.setSpacing( 5.0 );
        vBox.setOnMouseClicked( mouseEvent -> checkVisibleSet( mouseEvent, photoDataDto ) );

        ImageView imageView = new ImageView( image );
        imageView.setFitWidth( 90.0 );
        imageView.setFitHeight( 90.0 );
        imageView.setPickOnBounds( true );
        imageView.setPreserveRatio( true );
        imageView.setCursor( Cursor.HAND );

        Label label = new Label( title );
        label.setPrefWidth( 90.0 );
        label.setAlignment( Pos.CENTER );
        label.setTextAlignment( TextAlignment.CENTER );

        vBox.getChildren()
            .addAll( imageView, label );

        GridPane.setRowIndex( vBox, rowID );
        GridPane.setColumnIndex( vBox, columnID );
        previewGrid.getChildren()
            .add( vBox );
    }

    private void checkVisibleSet( MouseEvent event, PhotoDataDto photoDataDto )
    {
        String url = configURL.getDOWNLOAD_GIF_URL() + "/" + photoDataDto.getGifId();
        PhotoDataDto setPhotoDataDto = requestService.executeGetRequest( null, url, PhotoDataDto.class );

        // Fill the images list
        fillPhotoList( setPhotoDataDto );

        // Set the title of set of images
        setTitleLabel.setText( setPhotoDataDto.getTitle()
            .replace( "\"", "" ) );

        // Set main imageView on the scene
        mainImageView.setImage( images.get( 0 ) );

        // Set small imageViews in the scrollPane
        addImagesToScrollPane( images );
    }

    private void fillPhotoList( PhotoDataDto photoDataDto )
    {
        ByteArrayInputStream inputStream;
        Image image;
        images.clear();
        if( photoDataDto.getGif() != null )
        {
            inputStream = new ByteArrayInputStream( photoDataDto.getGif() );
            image = new Image( inputStream );
            images.add( image );
        }

        var photoSet = photoDataDto.getListOfPhotos();
        if( photoSet != null )
        {
            for( var photo : photoSet )
            {
                inputStream = new ByteArrayInputStream( photo );
                image = new Image( inputStream );
                images.add( image );
            }
        }
    }

    private void addImagesToScrollPane( List< Image > images )
    {
        miniHBox.getChildren()
            .clear();
        for( var image : images )
        {
            ImageView imageView = new ImageView( image );
            imageView.setFitHeight( 56.0 );
            imageView.setFitWidth( 110.0 );
            imageView.setPickOnBounds( true );
            imageView.setPreserveRatio( true );
            imageView.setOnMouseClicked( mouseEvent -> changeMainImageView( mouseEvent, image ) );

            miniHBox.getChildren()
                .add( imageView );
        }
    }

    private void changeMainImageView( MouseEvent mouseEvent, Image image )
    {
        mainImageView.setImage( image );
    }
}
