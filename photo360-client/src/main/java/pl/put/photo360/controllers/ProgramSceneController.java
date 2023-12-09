package pl.put.photo360.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dto.LabelsConstants;
import pl.put.photo360.dto.ToastsConstants;
import pl.put.photo360.handlers.AuthHandler;
import pl.put.photo360.service.RequestService;
import pl.put.photo360.toast.Toast;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// TODO - zrobić ogólny refactor klasy
@Component
public class ProgramSceneController extends SwitchSceneController implements Initializable {
    private String command;

    @FXML
    private ComboBox<String> comboList;

    @FXML
    private TextField textFieldDegree;

    @FXML
    private Slider sliderDegree;

    @FXML
    private Label labelDegree;

    @FXML
    private ListView<String> listViewCommands;

    @FXML
    private Button arrowPushUp;

    @FXML
    private Button arrowPushDown;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    private ObservableList<String> listItems = FXCollections.observableArrayList();

    @Autowired
    public ProgramSceneController(RequestService requestService, AuthHandler authHandler, Configuration configuration) {
        super(requestService, authHandler, configuration);
    }

    private void setSinglePhotoVisibility(Boolean flag) {
        textFieldDegree.setVisible(flag);
        textFieldDegree.setDisable(!flag);

        sliderDegree.setVisible(flag);
        sliderDegree.setDisable(!flag);

        labelDegree.setVisible(flag);
        labelDegree.setDisable(!flag);
    }

    private void updatePrefixesInList() {
        Pattern pattern = Pattern.compile("^(\\d+):\\s*");

        ObservableList<String> updatedList = IntStream.range(0, listItems.size())
                .mapToObj(i -> {
                    String currentItem = listItems.get(i);
                    String newItem = (i + 1) + ": " + pattern.matcher(currentItem).replaceAll("");
                    return newItem;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        listItems.setAll(updatedList);
    }

    private void updateMoveButtonsState() {
        int selectedIndex = listViewCommands.getSelectionModel().getSelectedIndex();
        arrowPushUp.setDisable(selectedIndex <= 0);
        arrowPushDown.setDisable(selectedIndex < 0 || selectedIndex >= listItems.size() - 1);
    }

    private void setupCommandCreator(String selectedItem) {
        // Usunięcie numeru z przodu i podział na podstawie pierwszej spacji
        String[] parts = selectedItem.substring(selectedItem.indexOf(":") + 1).trim().split(" ", 2);

        parts[0] += " " + parts[1].split(" ")[0];

        String commandType = parts[0]; // Zdjęcie 360 lub Pojedyncze zdjęcie
        int degrees = 0;

        // Sprawdzenie, czy jest to Pojedyncze zdjęcie z kątem
        if (commandType.equals(LabelsConstants.SINGLE_PHOTO.getPath()) && parts.length > 1) {
            try {
                // Ekstrakcja liczby stopni z drugiej części
                degrees = Integer.parseInt(parts[1].split(" ")[1]);
            } catch (NumberFormatException e) {
                // Możliwe, że będzie sie wywalalo przez new ActionEvent()
                Toast.showToast(new ActionEvent(), "Błąd podczas parsowania stopni");
            }
        }

        // Ustawienie wartości w kontrolkach
        comboList.setValue(commandType);
        textFieldDegree.setText(String.valueOf(degrees));
        sliderDegree.setValue(degrees);
        labelDegree.setText(String.valueOf(degrees) + LabelsConstants.DEGREE_SUFFIX.getPath());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboList.setValue(comboList.getItems().get(0));

        setSinglePhotoVisibility(false);

        listViewCommands.setItems(listItems);

        textFieldDegree.setText(String.valueOf((int) Math.round(sliderDegree.getValue())));

        sliderDegree.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                textFieldDegree.setText(String.valueOf((int) Math.round(new_val.doubleValue())));
            }
        });

        // Listener do obserwowania zmian w polu textFieldDegree
        textFieldDegree.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Sprawdzenie, czy nowa wartość zawiera tylko cyfry
                if (!newValue.matches("\\d*")) {
                    // Usunięcie wszystkich znaków niebędących cyframi
                    newValue = newValue.replaceAll("[^\\d]", "");
                }

                // Usunięcie wiodącego zera, jeśli jest obecne i wartość ma więcej niż jedną cyfrę
                if (newValue.startsWith("0") && newValue.length() > 1) {
                    newValue = newValue.substring(1);
                }

                // Jeśli nowa wartość nie jest pusta, sprawdzanie czy nie przekracza 360
                if (!newValue.isEmpty()) {
                    try {
                        int value = Integer.parseInt(newValue);
                        if (value > 360) {
                            // Powrót do starej wartości, jeśli nowa przekracza 360
                            newValue = oldValue;
                        }
                    } catch (NumberFormatException e) {
                        // Powrót do starej wartości w przypadku błędu konwersji
                        newValue = oldValue;
                    }
                }

                // Ograniczenie długości wartości do 3 cyfr
                if (newValue.length() > 3) {
                    newValue = oldValue;
                }

                // Ustawienie nowej wartości w polu tekstowym
                textFieldDegree.setText(newValue);

                // Synchronizacja wartości suwaka z polem tekstowym
                try {
                    sliderDegree.setValue(Integer.parseInt(newValue));
                } catch (NumberFormatException e) {
                    // Ustawienie wartości domyślnej, jeśli wystąpi błąd konwersji
                    textFieldDegree.setText("0");
                }
            }
        });


        listViewCommands.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateMoveButtonsState();
            deleteButton.setDisable(newSelection == null);
            editButton.setDisable(newSelection == null);
            if (newSelection != null) {
                setupCommandCreator(newSelection);
            }
        });
    }

    public void changeComboListElements(ActionEvent event) {
        String selectedType = comboList.getValue();

        if (selectedType.equals(LabelsConstants.PHOTO_360_DEGREE.getPath())) {
            setSinglePhotoVisibility(false);
        } else if (selectedType.equals(LabelsConstants.SINGLE_PHOTO.getPath())) {
            setSinglePhotoVisibility(true);
        }
    }

    public void addElementToList(ActionEvent event) {
        if (listItems.size() >= 10) {
            Toast.showToast(event, ToastsConstants.MAX_ITEMS_NUM.getPath());
            return;
        }
        String degree = "";
        String method = comboList.getValue();

        if (textFieldDegree.isVisible()) {
            degree = " " + textFieldDegree.getText() + LabelsConstants.DEGREE_SUFFIX.getPath()    ;
        }
        listItems.add(method + degree);
        updatePrefixesInList();
    }

    public void handleMoveUp() {
        int selectedIndex = listViewCommands.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            Collections.swap(listItems, selectedIndex, selectedIndex - 1);
            listViewCommands.getSelectionModel().select(selectedIndex - 1);
            updatePrefixesInList();
            listViewCommands.getSelectionModel().select(selectedIndex - 1);
        }
    }

    public void handleMoveDown() {
        int selectedIndex = listViewCommands.getSelectionModel().getSelectedIndex();
        if (selectedIndex < listItems.size() - 1) {
            Collections.swap(listItems, selectedIndex, selectedIndex + 1);
            listViewCommands.getSelectionModel().select(selectedIndex + 1);
            updatePrefixesInList();
            listViewCommands.getSelectionModel().select(selectedIndex + 1);
        }
    }

    public void removeActiveElement(ActionEvent event) {
        int selectedIndex = listViewCommands.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            ObservableList<String> items = listViewCommands.getItems();
            items.remove(selectedIndex);
            if (!items.isEmpty()) {
                updatePrefixesInList();
                listViewCommands.getSelectionModel().select(Math.min(selectedIndex, items.size() - 1));
            }
        }
    }

    public void editActiveElement(ActionEvent event) {
        int selectedIndex = listViewCommands.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String command = comboList.getValue();
            String degrees = textFieldDegree.getText();
            String updatedItem = (selectedIndex + 1) + ": " + command + (
                    command.equals(LabelsConstants.SINGLE_PHOTO.getPath()) ?
                            " " + degrees + LabelsConstants.DEGREE_SUFFIX.getPath() : "");

            listViewCommands.getItems().set(selectedIndex, updatedItem);
            updatePrefixesInList();

            String degree = "";
            String method = comboList.getValue();

            if (textFieldDegree.isVisible()) {
                degree = " " + textFieldDegree.getText() + LabelsConstants.DEGREE_SUFFIX.getPath();
            }
            listItems.set(selectedIndex, method + degree);
            updatePrefixesInList();
        }
    }
}
