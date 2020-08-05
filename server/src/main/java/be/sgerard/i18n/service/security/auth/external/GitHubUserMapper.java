package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.user.ExternalAuthSystem;
import be.sgerard.i18n.model.security.user.ExternalUser;
import be.sgerard.i18n.service.security.UserRole;
import com.jcabi.github.Organization;
import com.jcabi.github.RtGithub;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.json.JsonString;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toSet;

/**
 * {@link OAuthUserMapper Mapper} for user coming from GitHub.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubUserMapper implements OAuthUserMapper {

    /**
     * OAuth attribute containing the username.
     */
    public static final String USERNAME = "login";

    /**
     * OAuth attribute containing the display name.
     */
    public static final String NAME = "name";

    /**
     * OAuth attribute containing the email.
     */
    public static final String EMAIL = "email";

    /**
     * OAuth attribute containing the avatar URL.
     */
    public static final String AVATAR_URL = "avatar_url";

    /**
     * OAuth attribute containing the external ID.
     */
    public static final String EXTERNAL_ID = "node_id";

    private final AppProperties appProperties;

    public GitHubUserMapper(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public boolean support(OAuthExternalUser externalUser) {
        return externalUser.getOauthClient() == ExternalAuthSystem.OAUTH_GITHUB;
    }

    @Override
    public Mono<ExternalUser> map(OAuthExternalUser oAuthExternalUser) {
        final String email = getStringAttribute(oAuthExternalUser.getAttributes(), EMAIL);

        return Mono.just(
                ExternalUser.builder()
                        .externalId(getStringAttribute(oAuthExternalUser.getAttributes(), EXTERNAL_ID))
                        .authSystem(ExternalAuthSystem.OAUTH_GITHUB)
                        .username(getStringAttribute(oAuthExternalUser.getAttributes(), USERNAME))
                        .displayName(getStringAttribute(oAuthExternalUser.getAttributes(), NAME))
                        .email(email)
                        .avatarUrl(getStringAttribute(oAuthExternalUser.getAttributes(), AVATAR_URL))
                        .roles(isUserAllowed(email, oAuthExternalUser.getToken()) ? new UserRole[]{UserRole.MEMBER_OF_ORGANIZATION} : new UserRole[0])
                        .build()
        );
    }

    /**
     * Returns the string from the OAuth user's attributes.
     */
    private String getStringAttribute(Map<String, ?> attributes, String key) {
        if (attributes.containsKey(key)) {
            final Object value = attributes.get(key);

            if (value instanceof JsonString) {
                return ((JsonString) value).getString();
            } else {
                return Objects.toString(value);
            }
        } else {
            return null;
        }
    }

    /**
     * Returns whether the current user is allowed to access the application.
     */
    private boolean isUserAllowed(String email, String token) {
        final AppProperties.GitHubOauthOauth gitProperties = appProperties.getSecurity().getGithub();

        final Set<String> userOrganizations = StreamSupport
                .stream(new RtGithub(token).organizations().iterate().spliterator(), false)
                .map(Organization::login)
                .collect(toSet());

        return gitProperties.isEmailAllowed(email) && gitProperties.isOrganizationAllowed(userOrganizations);
    }
}
