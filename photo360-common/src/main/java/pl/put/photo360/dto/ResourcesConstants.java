package pl.put.photo360.dto;

public enum ResourcesConstants {
    SCENE_LOGIN("scenes/sceneLogowanie.fxml"),
    SCENE_REGISTER("scenes/sceneRejestracja.fxml"),
    SCENE_INFORMATIONS("scenes/sceneInformacje.fxml"),
    SCENE_MAIN("scenes/sceneMain.fxml"),
    SCENE_OPTIONS("scenes/sceneOpcje.fxml"),
    SCENE_PHOTOS("scenes/scenePhotos.fxml"),
    SCENE_RESET_PASSWORD("scenes/sceneResetPassword.fxml"),
    SCENE_SCENERIOS("scenes/sceneScenariusze.fxml"),
    IMAGE_APP_ICON("images/icon.jpg");
    private final String string;

    ResourcesConstants(String string) {
        this.string = string;
    }

    public String getPath() {
        return string;
    }
}
