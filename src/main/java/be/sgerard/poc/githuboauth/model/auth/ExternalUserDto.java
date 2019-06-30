package be.sgerard.poc.githuboauth.model.auth;

/**
 * @author Sebastien Gerard
 */
public class ExternalUserDto {

    private final String externalId;
    private final String username;
    private final String email;
    private final String avatarUrl;

    public ExternalUserDto(String externalId,
                           String username,
                           String email,
                           String avatarUrl) {
        this.externalId = externalId;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public String getExternalId() {
        return externalId;
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

    @Override
    public String toString() {
        return "ExternalUser(" + username + ", " + email + ')';
    }
}
