package be.sgerard.poc.githuboauth.model.auth;

/**
 * @author Sebastien Gerard
 */
public class UserDto {

    private final String username;
    private final String email;
    private final String avatarUrl;

    public UserDto(String username, String email, String avatarUrl) {
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
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
