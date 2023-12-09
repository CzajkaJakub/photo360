package pl.put.photo360.dto;

public enum LabelsConstants {
    APP_NAME("360 App"),
    SINGLE_PHOTO("Pojedyncze zdjęcie"),
    DEGREE_SUFFIX(" stopni"),
    PHOTO_360_DEGREE("Zdjęcie 360"),
    SEND_AGAIN("Wyślij ponownie");
    private final String string;

    LabelsConstants(String string) {
        this.string = string;
    }

    public String getPath() {
        return string;
    }
}
