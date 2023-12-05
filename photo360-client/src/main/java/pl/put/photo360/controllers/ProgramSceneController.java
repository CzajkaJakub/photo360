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
import org.springframework.stereotype.Controller;
import pl.put.photo360.dto.LoginResponseDtoStatic;
import pl.put.photo360.service.RequestService;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
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

    private ObservableList<String> listItems = FXCollections.observableArrayList();

    @Autowired
    public ProgramSceneController(RequestService requestService) {
        super(requestService);
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
        System.out.println("Elem 1: " + parts[0]);
        System.out.println("Elem 2: " + parts[1]);

        String commandType = parts[0]; // Zdjęcie 360 lub Pojedyncze zdjęcie
        int degrees = 0;

        // Sprawdzenie, czy jest to Pojedyncze zdjęcie z kątem
        if (commandType.equals("Pojedyncze zdjęcie") && parts.length > 1) {
            try {
                // Ekstrakcja liczby stopni z drugiej części
                degrees = Integer.parseInt(parts[1].split(" ")[1]);
            } catch (NumberFormatException e) {
                System.err.println("Błąd przy parsowaniu stopni: " + e.getMessage());
            }
        }

        // Ustawienie wartości w kontrolkach
        comboList.setValue(commandType);
        textFieldDegree.setText(String.valueOf(degrees));
        sliderDegree.setValue(degrees);
        labelDegree.setText(String.valueOf(degrees) + " stopni");
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

        textFieldDegree.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    newValue = newValue.replaceAll("[^\\d]", "");
                }
                if (newValue.startsWith("0") && newValue.length() > 1) {
                    newValue = newValue.substring(1);
                }
                if (!newValue.isEmpty()) {
                    try {
                        int value = Integer.parseInt(newValue);
                        if (value > 360) {
                            newValue = oldValue;
                        }
                    } catch (NumberFormatException e) {
                        newValue = oldValue;
                    }
                }
                if (newValue.length() > 3) {
                    newValue = oldValue;
                }
                textFieldDegree.setText(newValue);

                try {
                    sliderDegree.setValue(Integer.parseInt(newValue));
                } catch (NumberFormatException e) {
                    textFieldDegree.setText("0");
                }
            }
        });

        listViewCommands.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateMoveButtonsState();
            deleteButton.setDisable(newSelection == null);
            if (newSelection != null) {
                setupCommandCreator(newSelection);
            }
        });
    }

    public void changeComboListElements(ActionEvent event) {
        String selectedType = comboList.getValue();

        if (selectedType.equals("Zdjęcie 360")) {
            setSinglePhotoVisibility(false);
        } else if (selectedType.equals("Pojedyncze zdjęcie")) {
            setSinglePhotoVisibility(true);
        }
    }

    public void addElementToList(ActionEvent event) {
        if (listItems.size() >= 10) {
//            TODO Dodać wyskakujące powiadomienie o maksymalnym rozmiarze, coś jak Toast na mobilkach
            return;
        }
        String degree = "";
        String method = comboList.getValue();

        if (textFieldDegree.isVisible()) {
            degree = " " + textFieldDegree.getText() + " stopni";
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
}
