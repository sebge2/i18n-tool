package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.security.user.ExternalUserDto;
import be.sgerard.i18n.service.security.UserRole;
import com.jcabi.github.Coordinates;
import com.jcabi.github.RtGithub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
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

    private static final Logger logger = LoggerFactory.getLogger(ExternalGitHubUserExtractor.class);

    private final String repository;

    public ExternalGitHubUserExtractor(AppProperties appProperties) {
        this.repository = appProperties.getRepoFqnName();
    }

    @Override
    public boolean support(OAuth2UserRequest request) {
        return Objects.equals(request.getClientRegistration().getClientName(), GIT_HUB);
    }

    @Override
    public ExternalUserDto loadUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) throws BadCredentialsException {
        return loadUser(userRequest.getAccessToken().getTokenValue(), oAuth2User.getAttributes());
    }

    public ExternalUserDto loadUser(String tokenValue) throws UsernameNotFoundException {
        try {
            return loadUser(tokenValue, new RtGithub(tokenValue).users().self().json());
        } catch (Exception e) {
            logger.info("Error while connecting GitHub.", e);

            throw new AuthenticationServiceException("Cannot authenticate with the specified AuthKey.");
        } catch (AssertionError error) {
            logger.debug("Authentication failure.", error);

            throw new UsernameNotFoundException("Cannot authenticate with the specified AuthKey.");
        }
    }

    private ExternalUserDto loadUser(String tokenValue, Map<String, ?> attributes) {
        final boolean repoMember = isRepoMember(tokenValue);

        return ExternalUserDto.builder()
                .externalId(Objects.toString(attributes.get(EXTERNAL_ID)))
                .username(Objects.toString(attributes.get(LOGIN)))
                .email(Objects.toString(attributes.get(EMAIL)))
                .avatarUrl(Objects.toString(attributes.get(AVATAR_URL)))
                .gitHubToken(repoMember ? tokenValue : null)
                .roles(repoMember ? new UserRole[]{UserRole.MEMBER_OF_ORGANIZATION} : new UserRole[0])
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
