package pl.put.photo360.dto;

public enum ResourcesConstants {
    SCENE_LOGIN("/scenes/sceneLogin.fxml"),
    SCENE_REGISTER("/scenes/sceneRegister.fxml"),
    SCENE_INFORMATIONS("/scenes/sceneInformations.fxml"),
    SCENE_MAIN(
        "/scenes/sceneMain.fxml"),
    SCENE_OPTIONS("/scenes/sceneOptions.fxml"),
    SCENE_PHOTOS("/scenes/scenePhotos.fxml"),
    SCENE_RESET_PASSWORD("/scenes/sceneResetPassword.fxml"),
    SCENE_SCENERIOS("/scenes/sceneScenerios.fxml"),
    IMAGE_APP_ICON("/images/icon.jpg"),
    IMAGE_CAMERA_ICON("/images/camera_icon.png"),
    PYTHON_INTERPRETER_PATH("python"),
    PYTHON_BUFFOR_SWITCH("-u"),
    PYTHON_SCRIPT_PATH(System.getProperty("user.dir") + "\\photo360-driver\\pc\\main.py");
    private final String string;

    ResourcesConstants(String string) {
        this.string = string;
    }

    public String getPath() {
        return string;
    }
}
