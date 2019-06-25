package be.sgerard.poc.githuboauth.model.auth;

/**
 * @author Sebastien Gerard
 */
public class AuthenticationDto {

    private final String token;
    private final String username;
    private final String email;

    public AuthenticationDto(String token, String username, String email) {
        this.token = token;
        this.username = username;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
