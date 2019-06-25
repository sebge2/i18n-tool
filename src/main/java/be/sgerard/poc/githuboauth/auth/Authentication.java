package be.sgerard.poc.githuboauth.auth;

/**
 * @author Sebastien Gerard
 */
public class Authentication {

    private final String token;
    private final String username;
    private final String email;

    public Authentication(String token, String username, String email) {
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
