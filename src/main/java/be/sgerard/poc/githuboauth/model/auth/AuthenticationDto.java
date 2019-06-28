package be.sgerard.poc.githuboauth.model.auth;

/**
 * @author Sebastien Gerard
 */
public class AuthenticationDto {

    private final String token;
    private final String username;
    private final String email;
    private final String avatarUrl;

    public AuthenticationDto(String token, String username, String email, String avatarUrl) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
