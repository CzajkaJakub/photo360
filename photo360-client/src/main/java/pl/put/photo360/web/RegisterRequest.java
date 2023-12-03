package pl.put.photo360.web;

public class RegisterRequest {
    private String login;
    private String email;
    private String password;

    public RegisterRequest(String login, String email, String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}