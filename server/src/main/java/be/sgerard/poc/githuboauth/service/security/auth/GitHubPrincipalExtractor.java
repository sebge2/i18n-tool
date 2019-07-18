package be.sgerard.poc.githuboauth.service.security.auth;

import be.sgerard.poc.githuboauth.model.security.user.ExternalUserDto;
import be.sgerard.poc.githuboauth.model.security.user.UserDto;
import be.sgerard.poc.githuboauth.service.security.user.UserManager;
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

    private final UserManager userManager;

    public GitHubPrincipalExtractor(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public UserDto extractPrincipal(Map<String, Object> map) {
        final ExternalUserDto externalUserDto = new ExternalUserDto(
                Objects.toString(map.get(EXTERNAL_ID)),
                Objects.toString(map.get(LOGIN)),
                Objects.toString(map.get(EMAIL)),
                Objects.toString(map.get(AVATAR_URL))
        );

        return UserDto.builder(userManager.createOrUpdateUser(externalUserDto)).build();
    }
}
