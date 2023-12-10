package pl.put.photo360.dto;

public enum ToastsConstants {
    MAX_ITEMS_NUM("Maksymalna liczba elementów w liście wynosi 10"),
    NOT_THE_SAME_PASSWORDS("Hasła nie są takie same"),
    PASSWORD_CHANGED("Hasło zostało pomyślnie zmienione"),
    USER_NOT_FOUND_BY_LOGIN("Nie znaleziono użytkownika o podanym loginie"),
    WRONG_FIELDS_SIZE("Co najmniej jedno z pól jest nieprawidłowe"),
    WRONG_CREDENTIALS("ERROR"), // TODO
    AUTH_TOKEN_EXPIRED("Token autoryzacyjny wygasł"),
    RESET_TOKEN_EXPIRED("Token resetowania hasła wygasł"),
    AUTH_TOKEN_NOT_VALID("Nieprawidłowy token autoryzacyjny"),
    WRONG_PUBLIC_API_KEY("Nieprawidłowy klucz publiczny"),
    UNAUTHORIZED_ROLE("Nie posiadasz uprawnień do tej akcji"), // TODO
    DELETE_NOT_ALLOWED("Nie można usunąć elementu"),
    ADD_TO_FAVOURITE_NOT_ALLOWED("Nie można dodać elementu do ulubionych"),
    PASSWORD_CAN_NOT_BE_THE_SAME("Nowe hasło nie może być identyczne"),
    VALIDATION_NOT_PASSED("Walidacja nie powiodła się"), // TODO
    EMAIL_ALREADY_VERIFIED("Adres email został już zweryfikowany"),
    EMAIL_VERIFICATION_TOKEN_NOT_VALID("Token weryfikacyjny jest nieprawidłowy"),
    EMAIL_VERIFICATION_TOKEN_EXPIRED("Token weryfikacyjny wygasł"),
    GIF_IS_NOT_PUBLIC("GIF nie jest publiczny"), //TODO
    FIELD_CONTAINS_WHITESPACES("Co najmniej jedno pole zawiera spacje"), // TODO
    STATUS_MISSING_REQUIRED_FIELD("Proszę uzupełnić wszystkie pola"), // TODO
    EMAIL_WRONG_FORMAT("Niepoprawny format adresu email"),
    UNSUPPORTED("Nie wspierane"), // TODO
    WRONG_FILE_FORMAT("Niepoprawny format danych"),
    CONNECTION_REFUSED("Połączenie zerwane"), // TODO
    USER_NOT_FOUND_BY_EMAIL("Nie znaleziono użytkownika o podanym adresie email"),
    USER_NOT_FOUND_FROM_TOKEN("Nie znaleziono użytkownika o podanym tokenie"), // TODO
    GIF_BY_GIVEN_ID_NOT_EXISTS("GIF o podanym ID nie istnieje"),
    GIVEN_EMAIL_EXISTS("Podany adres email jest już używany"),
    GIVEN_LOGIN_EXISTS("Podana nazwa użytkownika jest już używana"),
    ACCOUNT_LOCKED("Konto zostało zablokowane z powodu zbyt wielu nieudanych prób logowania"),
    EMAIL_NOT_CONFIRMED("Akcja niedostępna z powodu niepotwierdzonego adresu email"), // TODO
    EMAIL_SEND_FAILED("Wysłanie wiadomości email nie powiodło się"), // TODO
    GIF_ALREADY_ADDED_TO_FAVOURITE("GIF został już dodany do ulubionych"),
    DEFAULT_ERROR("Nieznany błąd"); // TODO

    private final String string;

    ToastsConstants(String string) {
        this.string = string;
    }

    public String getPath() {
        return string;
    }
}