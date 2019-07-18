package be.sgerard.poc.githuboauth.service.security.auth;

import be.sgerard.poc.githuboauth.model.security.user.ExternalUserDto;
import be.sgerard.poc.githuboauth.model.security.user.UserDto;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;
import java.util.Objects;

/**
 * @author Sebastien Gerard
 */
public class GitHubPrincipalExtractor implements PrincipalExtractor {

    public static final String LOGIN = "login";

    public static final String EMAIL = "email";

    public static final String AVATAR_URL = "avatar_url";

    public static final String EXTERNAL_ID = "node_id";

    private final AuthenticationManager authenticationManager;

    public GitHubPrincipalExtractor(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDto extractPrincipal(Map<String, Object> map) {
        final ExternalUserDto externalUserDto = new ExternalUserDto(
                Objects.toString(map.get(EXTERNAL_ID)),
                Objects.toString(map.get(LOGIN)),
                Objects.toString(map.get(EMAIL)),
                Objects.toString(map.get(AVATAR_URL))
        );

        return UserDto.builder(authenticationManager.createOrUpdateUser(externalUserDto)).build();
    }
}
