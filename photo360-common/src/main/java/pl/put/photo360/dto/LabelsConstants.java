package pl.put.photo360.dto;

public enum LabelsConstants {
    APP_NAME("360 App"),
    SINGLE_PHOTO("Pojedyncze zdjęcie"),
    DEGREE_SUFFIX(" stopni"),
    PHOTO_360_DEGREE("Zdjęcie 360"),
    SEND_AGAIN("Wyślij ponownie"),
    SINGLE_MOVE_COMMAND("single_move"),
    FULL_MOVE_COMMAND("full_move"),
    BASE_STEP_FULL_MOVE("10"),
    CHOOSE_PARENT_DIRECTORY("Wybierz katalog nadrzędny dla nowego folderu"),
    CHOOSE_MAIN_PHOTO_FOLDER("Wybierz główny katalog ze zdjęciami"),
    RADIO_LABEL_PRIVATE("Prywatne"),
    RADIO_LABEL_PUBLIC("Publiczne"),
    SEND_PHOTOS_DIALOG_TITLE("Jeszcze parę informacji"),
    CONFIRM_BUTTON_LABEL("Zatwierdź"),
    CHOOSE_PARENT_DIRECTORY_BASE("NowyFolder"),
    DIRECTORY_NAME_TITLE("Nazwa nowego katalogu"),
    DIRECTORY_NAME_HEADER("Wprowadź nazwę dla nowego katalogu:"),
    DIRECTORY_NAME_TEXT("Nazwa:"),
    DIRECTORY_ERROR_TITLE("Błąd"),
    DIRECTORY_ERROR_HEADER("Folder już istnieje"),
    VERIFIED_LABEL("zweryfikowany"),
    DIRECTORY_ERROR_TEXT("Folder o podanej nazwie już istnieje w wybranej lokalizacji. Podaj inną nazwę."),
    CAMERA_STAGE_TITLE("Podgląd Kamery");

    private final String string;

    LabelsConstants(String string) {
        this.string = string;
    }

    public String getLabel() {
        return string;
    }
}
