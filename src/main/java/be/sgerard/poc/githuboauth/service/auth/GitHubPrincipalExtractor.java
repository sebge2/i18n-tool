package be.sgerard.poc.githuboauth.service.auth;

import be.sgerard.poc.githuboauth.model.auth.UserDto;
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

    public GitHubPrincipalExtractor() {

    }

    @Override
    public UserDto extractPrincipal(Map<String, Object> map) {
        return new UserDto(
                Objects.toString(map.get(LOGIN)),
                Objects.toString(map.get(EMAIL)),
                Objects.toString(map.get(AVATAR_URL))
        );
    }
}
