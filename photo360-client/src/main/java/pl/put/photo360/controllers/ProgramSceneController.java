package pl.put.photo360.controllers;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import pl.put.photo360.camera.view.CameraWindow;
import pl.put.photo360.config.ConfigURL;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dialog.DialogData;
import pl.put.photo360.dto.LabelsConstants;
import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.dto.ResourcesConstants;
import pl.put.photo360.dto.ToastsConstants;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.handlers.ZipHandler;
import pl.put.photo360.service.RequestService;
import pl.put.photo360.toast.Toast;

// TODO - zrobić ogólny refactor klasy
@Component
public class ProgramSceneController extends SwitchSceneController implements Initializable
{
    private String command;

    @FXML
    private ComboBox< String > comboList;

    @FXML
    private TextField textFieldDegree;

    @FXML
    private Slider sliderDegree;

    @FXML
    private Label labelDegree;

    @FXML
    private ListView< String > listViewCommands;

    @FXML
    private Button arrowPushUp;

    @FXML
    private Button arrowPushDown;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button cameraButton;

    @FXML
    private Button runButton;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button sendButton;

    private ZipHandler zipHandler;

    private ObservableList< String > listItems = FXCollections.observableArrayList();

    @Autowired
    public ProgramSceneController( RequestService requestService, AuthHandler authHandler,
        Configuration configuration, ConfigURL configURL, CameraWindow cameraWindow, ZipHandler zipHandler )
    {
        super( requestService, authHandler, configuration, configURL, cameraWindow );
        this.zipHandler = zipHandler;
    }

    private void setSinglePhotoVisibility( Boolean flag )
    {
        textFieldDegree.setVisible( flag );
        textFieldDegree.setDisable( !flag );

        sliderDegree.setVisible( flag );
        sliderDegree.setDisable( !flag );

        labelDegree.setVisible( flag );
        labelDegree.setDisable( !flag );
    }

    private void updatePrefixesInList()
    {
        Pattern pattern = Pattern.compile( "^(\\d+):\\s*" );

        ObservableList< String > updatedList = IntStream.range( 0, listItems.size() )
            .mapToObj( i -> {
                String currentItem = listItems.get( i );
                String newItem = (i + 1) + ": " + pattern.matcher( currentItem )
                    .replaceAll( "" );
                return newItem;
            } )
            .collect( Collectors.toCollection( FXCollections::observableArrayList ) );

        listItems.setAll( updatedList );
    }

    private void updateMoveButtonsState()
    {
        int selectedIndex = listViewCommands.getSelectionModel()
            .getSelectedIndex();
        arrowPushUp.setDisable( selectedIndex <= 0 );
        arrowPushDown.setDisable( selectedIndex < 0 || selectedIndex >= listItems.size() - 1 );
    }

    private void setupCommandCreator( String selectedItem )
    {
        // Usunięcie numeru z przodu i podział na podstawie pierwszej spacji
        String[] parts = selectedItem.substring( selectedItem.indexOf( ":" ) + 1 )
            .trim()
            .split( " ", 2 );

        parts[ 0 ] += " " + parts[ 1 ].split( " " )[ 0 ];

        String commandType = parts[ 0 ]; // Zdjęcie 360 lub Pojedyncze zdjęcie
        int degrees = 0;

        // Sprawdzenie, czy jest to Pojedyncze zdjęcie z kątem
        if( commandType.equals( LabelsConstants.SINGLE_PHOTO.getLabel() ) && parts.length > 1 )
        {
            try
            {
                // Ekstrakcja liczby stopni z drugiej części
                degrees = Integer.parseInt( parts[ 1 ].split( " " )[ 1 ] );
            }
            catch( NumberFormatException e )
            {
                // Możliwe, że będzie sie wywalalo przez new ActionEvent()
                Toast.showToast( new ActionEvent(), "Błąd podczas parsowania stopni" );
            }
        }

        // Ustawienie wartości w kontrolkach
        comboList.setValue( commandType );
        textFieldDegree.setText( String.valueOf( degrees ) );
        sliderDegree.setValue( degrees );
        labelDegree.setText( String.valueOf( degrees ) + LabelsConstants.DEGREE_SUFFIX.getLabel() );
    }

    @Override
    public void initialize( URL url, ResourceBundle resourceBundle )
    {
        comboList.setValue( comboList.getItems()
            .get( 0 ) );

        setSinglePhotoVisibility( false );

        listViewCommands.setItems( listItems );

        textFieldDegree.setText( String.valueOf( (int)Math.round( sliderDegree.getValue() ) ) );

        // Set imageView on button
        Image img = new Image( ResourcesConstants.IMAGE_CAMERA_ICON.getPath() );
        ImageView view = new ImageView( img );
        view.setFitHeight( 20 );
        view.setPreserveRatio( true );
        // Setting the size of the button
        // Setting a graphic to the button
        cameraButton.setGraphic( view );

        sliderDegree.valueProperty()
            .addListener( new ChangeListener< Number >()
            {
                public void changed( ObservableValue< ? extends Number > ov, Number old_val, Number new_val )
                {
                    textFieldDegree.setText( String.valueOf( (int)Math.round( new_val.doubleValue() ) ) );
                }
            } );

        // Listener do obserwowania zmian w polu textFieldDegree
        textFieldDegree.textProperty()
            .addListener( new ChangeListener< String >()
            {
                @Override
                public void changed( ObservableValue< ? extends String > observable, String oldValue,
                    String newValue )
                {
                    // Sprawdzenie, czy nowa wartość zawiera tylko cyfry
                    if( !newValue.matches( "\\d*" ) )
                    {
                        // Usunięcie wszystkich znaków niebędących cyframi
                        newValue = newValue.replaceAll( "[^\\d]", "" );
                    }

                    // Usunięcie wiodącego zera, jeśli jest obecne i wartość ma więcej niż jedną cyfrę
                    if( newValue.startsWith( "0" ) && newValue.length() > 1 )
                    {
                        newValue = newValue.substring( 1 );
                    }

                    // Jeśli nowa wartość nie jest pusta, sprawdzanie czy nie przekracza 360
                    if( !newValue.isEmpty() )
                    {
                        try
                        {
                            int value = Integer.parseInt( newValue );
                            if( value > 360 )
                            {
                                // Powrót do starej wartości, jeśli nowa przekracza 360
                                newValue = oldValue;
                            }
                        }
                        catch( NumberFormatException e )
                        {
                            // Powrót do starej wartości w przypadku błędu konwersji
                            newValue = oldValue;
                        }
                    }

                    // Ograniczenie długości wartości do 3 cyfr
                    if( newValue.length() > 3 )
                    {
                        newValue = oldValue;
                    }

                    // Ustawienie nowej wartości w polu tekstowym
                    textFieldDegree.setText( newValue );

                    // Synchronizacja wartości suwaka z polem tekstowym
                    try
                    {
                        sliderDegree.setValue( Integer.parseInt( newValue ) );
                    }
                    catch( NumberFormatException e )
                    {
                        // Ustawienie wartości domyślnej, jeśli wystąpi błąd konwersji
                        textFieldDegree.setText( "0" );
                    }
                }
            } );

        listViewCommands.getSelectionModel()
            .selectedItemProperty()
            .addListener( ( obs, oldSelection, newSelection ) -> {
                updateMoveButtonsState();
                deleteButton.setDisable( newSelection == null );
                editButton.setDisable( newSelection == null );
                if( newSelection != null )
                {
                    setupCommandCreator( newSelection );
                }
            } );
    }

    public void changeComboListElements( ActionEvent event )
    {
        String selectedType = comboList.getValue();

        if( selectedType.equals( LabelsConstants.PHOTO_360_DEGREE.getLabel() ) )
        {
            setSinglePhotoVisibility( false );
        }
        else if( selectedType.equals( LabelsConstants.SINGLE_PHOTO.getLabel() ) )
        {
            setSinglePhotoVisibility( true );
        }
    }

    public void addElementToList( ActionEvent event )
    {
        if( listItems.size() >= 10 )
        {
            Toast.showToast( event, ToastsConstants.MAX_ITEMS_NUM.getMessage() );
            return;
        }

        for( var listItem : listItems )
        {
            if( listItem.split( " ", 2 )[ 1 ].equals( comboList.getValue() ) )
            {
                Toast.showToast( event, ToastsConstants.ONLY_ONE_PHOTO_360.getMessage() );
                return;
            }
        }

        String degree = "";
        String method = comboList.getValue();

        if( textFieldDegree.isVisible() )
        {
            degree = " " + textFieldDegree.getText() + LabelsConstants.DEGREE_SUFFIX.getLabel();
        }
        listItems.add( method + degree );
        updatePrefixesInList();
    }

    public void handleMoveUp()
    {
        int selectedIndex = listViewCommands.getSelectionModel()
            .getSelectedIndex();
        if( selectedIndex > 0 )
        {
            Collections.swap( listItems, selectedIndex, selectedIndex - 1 );
            listViewCommands.getSelectionModel()
                .select( selectedIndex - 1 );
            updatePrefixesInList();
            listViewCommands.getSelectionModel()
                .select( selectedIndex - 1 );
        }
    }

    public void handleMoveDown()
    {
        int selectedIndex = listViewCommands.getSelectionModel()
            .getSelectedIndex();
        if( selectedIndex < listItems.size() - 1 )
        {
            Collections.swap( listItems, selectedIndex, selectedIndex + 1 );
            listViewCommands.getSelectionModel()
                .select( selectedIndex + 1 );
            updatePrefixesInList();
            listViewCommands.getSelectionModel()
                .select( selectedIndex + 1 );
        }
    }

    public void removeActiveElement( ActionEvent event )
    {
        int selectedIndex = listViewCommands.getSelectionModel()
            .getSelectedIndex();
        if( selectedIndex != -1 )
        {
            ObservableList< String > items = listViewCommands.getItems();
            items.remove( selectedIndex );
            if( !items.isEmpty() )
            {
                updatePrefixesInList();
                listViewCommands.getSelectionModel()
                    .select( Math.min( selectedIndex, items.size() - 1 ) );
            }
        }
    }

    public void editActiveElement( ActionEvent event )
    {
        int selectedIndex = listViewCommands.getSelectionModel()
            .getSelectedIndex();
        if( selectedIndex != -1 )
        {
            String command = comboList.getValue();
            String degrees = textFieldDegree.getText();
            String updatedItem = (selectedIndex + 1) + ": " + command
                + (command.equals( LabelsConstants.SINGLE_PHOTO.getLabel() )
                    ? " " + degrees + LabelsConstants.DEGREE_SUFFIX.getLabel()
                    : "");

            listViewCommands.getItems()
                .set( selectedIndex, updatedItem );
            updatePrefixesInList();

            String degree = "";
            String method = comboList.getValue();

            if( textFieldDegree.isVisible() )
            {
                degree = " " + textFieldDegree.getText() + LabelsConstants.DEGREE_SUFFIX.getLabel();
            }
            listItems.set( selectedIndex, method + degree );
            updatePrefixesInList();
        }
    }

    private List< String > buildCommandFromListItems()
    {
        // Create a list of commands based on the contents of listItems
        List< String > commands = new ArrayList<>();
        for( String item : listItems )
        {
            String[] parts = item.split( " " );
            String begin = String.join( " ", parts[ 1 ], parts[ 2 ] );

            if( begin.equals( LabelsConstants.PHOTO_360_DEGREE.getLabel() ) )
            {
                commands.add( LabelsConstants.FULL_MOVE_COMMAND.getLabel() );
                commands.add( LabelsConstants.BASE_STEP_FULL_MOVE.getLabel() );
            }
            else if( begin.equals( LabelsConstants.SINGLE_PHOTO.getLabel() ) )
            {
                commands.add( LabelsConstants.SINGLE_MOVE_COMMAND.getLabel() );
                commands.add( parts[ 3 ] );
            }
        }

        String foldername = chooseDirectoryAndName();

        // Adding the python path and script to the command list
        commands.add( 0, foldername );
        commands.add( 0, ResourcesConstants.PYTHON_SCRIPT_PATH.getPath() );
        commands.add( 0, ResourcesConstants.PYTHON_BUFFOR_SWITCH.getPath() );
        commands.add( 0, ResourcesConstants.PYTHON_INTERPRETER_PATH.getPath() );
        return commands;
    }

    private String chooseDirectoryAndName()
    {
        // Wybierz katalog nadrzędny
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle( LabelsConstants.CHOOSE_PARENT_DIRECTORY.getLabel() );
        File selectedDirectory = directoryChooser.showDialog( null );

        if( selectedDirectory != null )
        {
            while( true )
            {
                // Poproś o nazwę nowego katalogu
                TextInputDialog dialog =
                    new TextInputDialog( LabelsConstants.CHOOSE_PARENT_DIRECTORY_BASE.getLabel() );
                dialog.setTitle( LabelsConstants.DIRECTORY_NAME_TITLE.getLabel() );
                dialog.setHeaderText( LabelsConstants.DIRECTORY_NAME_HEADER.getLabel() );
                dialog.setContentText( LabelsConstants.DIRECTORY_NAME_TEXT.getLabel() );

                Optional< String > result = dialog.showAndWait();

                if( result.isPresent() )
                {
                    String folderName = result.get();
                    String fullPath = selectedDirectory.getAbsolutePath() + File.separator + folderName;

                    File newFolder = new File( fullPath );
                    if( newFolder.exists() )
                    {
                        // Informowanie użytkownika, że folder już istnieje
                        Alert alert = new Alert( Alert.AlertType.ERROR );
                        alert.setTitle( LabelsConstants.DIRECTORY_ERROR_TITLE.getLabel() );
                        alert.setHeaderText( LabelsConstants.DIRECTORY_ERROR_HEADER.getLabel() );
                        alert.setContentText( LabelsConstants.DIRECTORY_ERROR_TEXT.getLabel() );
                        alert.showAndWait();
                    }
                    else
                    {
                        return fullPath; // Zwróć pełną ścieżkę do nowego katalogu
                    }
                }
                else
                {
                    // Jeśli użytkownik anuluje operację, zwróć pusty ciąg znaków
                    return "";
                }
            }
        }
        else
        {
            // Jeśli użytkownik nie wybrał katalogu nadrzędnego
            return "";
        }
    }

    public void runPython( ActionEvent event )
    {
        try
        {
            List< String > commands = buildCommandFromListItems();
            String folderPath = commands.get( 3 );
            if( folderPath.equals( "" ) )
            {
                return;
            }

            mainPane.setDisable( true );
            ProcessBuilder pb = new ProcessBuilder( commands );
            pb.redirectErrorStream(true);
            Process p = pb.start();

//            int exitCode = p.waitFor();
//
//            switch( exitCode )
//            {
//                case 101 -> Toast.showToast( event, ToastsConstants.EMPTY_LIST_ITEMS.getMessage() );
//                case 102 -> Toast.showToast( event, ToastsConstants.CONNECTION_ERROR.getMessage() );
//                case 103 -> Toast.showToast( event, ToastsConstants.CAMERA_NOT_FOUND.getMessage() );
//                case 104 -> Toast.showToast( event, ToastsConstants.FOLDER_ALREADY_EXISTS.getMessage() );
//            }
//            mainPane.setDisable( false );

            // Odczytanie wyjścia procesu
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                switch( Integer.parseInt(line.substring(0, 3)) )
                {
                    case 101 -> Toast.showToast( event, ToastsConstants.EMPTY_LIST_ITEMS.getMessage() );
                    case 102 -> Toast.showToast( event, ToastsConstants.CONNECTION_ERROR.getMessage() );
                    case 103 -> Toast.showToast( event, ToastsConstants.CAMERA_NOT_FOUND.getMessage() );
                    case 104 -> Toast.showToast( event, ToastsConstants.FOLDER_ALREADY_EXISTS.getMessage() );
                }
            }

            p.waitFor();
            mainPane.setDisable( false );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    private boolean checkDirectoryForPhotos( File directory, boolean first_check )
    {
        if( directory.exists() && directory.isDirectory() )
        {
            // Filtr plików, który akceptuje pliki z rozszerzeniami .jpg, .png, .bmp itp.
            FilenameFilter imageFilter = ( dir, name ) -> name.endsWith( ".jpg" ) || name.endsWith( ".png" );

            // Pobierz pliki, które pasują do filtru
            File[] files = directory.listFiles( imageFilter );

            // Sprawdź, czy są jakieś pliki zdjęć
            return files != null && files.length > 0;
        }
        return !first_check;
    }

    public void sendPhotos( ActionEvent event )
    {
        // Inicjowanie potrzebnych zmiennych
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle( LabelsConstants.CHOOSE_MAIN_PHOTO_FOLDER.getLabel() );
        File selectedDirectory = directoryChooser.showDialog( null );
        File single = new File( selectedDirectory, "single" );
        File full = new File( selectedDirectory, "full" );

        // Sprawdzenie, czy struktura katalogów jest poprawna
        boolean exists_single = checkDirectoryForPhotos( single, true );
        boolean exists_full = checkDirectoryForPhotos( full, true );

        if( !exists_full && !exists_single || !checkDirectoryForPhotos( full, false )
            || !checkDirectoryForPhotos( single, false ) )
        {
            Toast.showToast( event, ToastsConstants.WRONG_CATALOG_WAS_CHOOSEN.getMessage() );
            return;
        }

        // Pobieranie tytułu, opisu oraz trybu widoczności zdjęć od użytkownika
        AtomicBoolean isPublic = new AtomicBoolean( false );
        AtomicReference< String > title = new AtomicReference<>( "" );
        AtomicReference< String > description = new AtomicReference<>( "" );

        Dialog< DialogData > dialog = createDialog();
        dialog.showAndWait()
            .ifPresent( response -> {
                isPublic.set( response.isAccessTypePublic() );
                title.set( response.getTitle() );
                description.set( response.getDescription() );
            } );
        if( title.get()
            .isBlank() )
        {
            return;
        }

        // Tworzenie i przypisywanie zipów
        FileSystemResource resourceSinglePhotos;
        FileSystemResource resourceFullMove;
        try
        {
            resourceSinglePhotos = zipHandler.createZipOfImages( single.getPath(), "\\single.zip" );
            resourceFullMove = zipHandler.createZipOfImages( full.getPath(), "\\full.zip" );
        }
        catch( IOException ioException )
        {
            Toast.showToast( event, ToastsConstants.FAILED_ZIP_CREATION.getMessage() );
            return;
        }

        // Budowanie ciała zapytania
        MultiValueMap< String, Object > body = new LinkedMultiValueMap<>();
        body.add( "isPublic", isPublic ); // lub false, zależnie od wymagania [REQUIRED]
        body.add( "photosZipFile", resourceSinglePhotos ); // Dodajemy pojedyncze zdjęcia jako resource [NOT
                                                           // REQUIRED]
        body.add( "photosZipFile360", resourceFullMove ); // Dodajemy zdjęcia do 360 jako resource [NOT
                                                          // REQUIRED]
        body.add( "description", description ); // [REQUIRED]
        body.add( "title", title ); // [REQUIRED]
        body.add( "backgroundColor", "#FFFFFF" ); // [REQUIRED]

        // Wykonanie zapytania
        requestService.executeRequest( event, body, configURL.getUPLOAD_PHOTOS_URL(),
            RequestResponseDto.class );

        // Usuwanie zipów
        zipHandler.deleteZipFiles( single.getPath() );
        zipHandler.deleteZipFiles( full.getPath() );
    }

    public void startWindowCamera( ActionEvent event )
    {
//        Node source = (Node)event.getSource();
//        Stage stage = (Stage)source.getScene()
//            .getWindow();
//        cameraWindow.startCamera( stage );
        Toast.showToast(event, ToastsConstants.TEMP_UNAVAILABLE.getMessage());
    }

    private Dialog< DialogData > createDialog()
    {
        Dialog< DialogData > dialog = new Dialog<>();
        dialog.setTitle( LabelsConstants.SEND_PHOTOS_DIALOG_TITLE.getLabel() );

        // Dodaj przyciski
        ButtonType confirmButtonType =
            new ButtonType( LabelsConstants.CONFIRM_BUTTON_LABEL.getLabel(), ButtonBar.ButtonData.OK_DONE );
        dialog.getDialogPane()
            .getButtonTypes()
            .addAll( confirmButtonType, ButtonType.CANCEL );

        // Utwórz formularz
        GridPane grid = new GridPane();
        grid.setHgap( 10 );
        grid.setVgap( 10 );
        grid.setPadding( new Insets( 20, 150, 10, 10 ) );

        RadioButton rbPrivate = new RadioButton( LabelsConstants.RADIO_LABEL_PRIVATE.getLabel() );
        rbPrivate.setSelected( true );
        RadioButton rbPublic = new RadioButton( LabelsConstants.RADIO_LABEL_PUBLIC.getLabel() );
        ToggleGroup accessGroup = new ToggleGroup();
        rbPrivate.setToggleGroup( accessGroup );
        rbPublic.setToggleGroup( accessGroup );

        TextField titleField = new TextField();
        TextField descriptionField = new TextField();

        grid.add( new Label( "Widoczność zdjęć:" ), 0, 0 );
        grid.add( rbPrivate, 1, 0 );
        grid.add( rbPublic, 2, 0 );
        grid.add( new Label( "Tytuł:" ), 0, 1 );
        grid.add( titleField, 1, 1, 2, 1 );
        grid.add( new Label( "Opis:" ), 0, 2 );
        grid.add( descriptionField, 1, 2, 2, 1 );

        Node confirmButton = dialog.getDialogPane()
            .lookupButton( confirmButtonType );
        confirmButton.setDisable( true );

        titleField.textProperty()
            .addListener( ( observable, oldValue, newValue ) -> {
                confirmButton.setDisable( newValue.trim()
                    .isEmpty() );
            } );

        dialog.getDialogPane()
            .setContent( grid );

        dialog.setResultConverter( new Callback< ButtonType, DialogData >()
        {
            @Override
            public DialogData call( ButtonType button )
            {
                if( button == confirmButtonType )
                {
                    boolean accessTypePublic = rbPublic.isSelected(); // true jeśli publiczny, false jeśli
                                                                      // prywatny
                    return new DialogData( accessTypePublic, titleField.getText(),
                        descriptionField.getText() );
                }
                return null;
            }
        } );

        return dialog;
    }
}
