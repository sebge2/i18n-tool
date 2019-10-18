package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.security.user.ExternalUserDto;
import com.jcabi.github.Coordinates;
import com.jcabi.github.RtGithub;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Sebastien Gerard
 */
@Component
public class ExternalGitHubUserExtractor implements ExternalUserExtractor {

    public static final String LOGIN = "login";

    public static final String EMAIL = "email";

    public static final String AVATAR_URL = "avatar_url";

    public static final String EXTERNAL_ID = "node_id";

    public static final String GIT_HUB = "GitHub";

    private final String repository;

    public ExternalGitHubUserExtractor(AppProperties appProperties) {
        this.repository = appProperties.getRepoFqnName();
    }

    @Override
    public boolean support(OAuth2UserRequest request) {
        return Objects.equals(request.getClientRegistration().getClientName(), GIT_HUB);
    }

    @Override
    public ExternalUserDto loadUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        final String tokenValue = userRequest.getAccessToken().getTokenValue();

        return ExternalUserDto.builder()
                .externalId(Objects.toString(oAuth2User.getAttributes().get(EXTERNAL_ID)))
                .username(Objects.toString(oAuth2User.getAttributes().get(LOGIN)))
                .email(Objects.toString(oAuth2User.getAttributes().get(EMAIL)))
                .avatarUrl(Objects.toString(oAuth2User.getAttributes().get(AVATAR_URL)))
                .gitHubToken(isRepoMember(tokenValue) ? tokenValue : null)
                .build();
    }

    private boolean isRepoMember(String tokenValue) {
        final RtGithub github = new RtGithub(tokenValue);

        try {
            return github.repos().get(new Coordinates.Simple(repository)).branches().iterate().iterator().hasNext();
        } catch (AssertionError e) {
            return false;
        }
    }
}
