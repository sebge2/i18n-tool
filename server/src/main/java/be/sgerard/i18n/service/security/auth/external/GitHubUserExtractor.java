package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.client.repository.github.GitHubClient;
import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.security.auth.external.RawExternalUser;
import be.sgerard.i18n.model.security.user.ExternalAuthSystem;
import be.sgerard.i18n.model.security.user.ExternalUser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.json.JsonString;
import java.util.Map;
import java.util.Objects;

/**
 * {@link ExternalUserExtractor Extractor} of users coming from GitHub.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubUserExtractor implements ExternalUserExtractor {

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
    private final GitHubClient gitHubClient;

    public GitHubUserExtractor(AppProperties appProperties, GitHubClient gitHubClient) {
        this.appProperties = appProperties;
        this.gitHubClient = gitHubClient;
    }

    @Override
    public boolean support(RawExternalUser externalUser) {
        return externalUser.getAuthSystem() == ExternalAuthSystem.OAUTH_GITHUB;
    }

    @Override
    public Mono<ExternalUser> map(RawExternalUser rawExternalUser) {
        final String email = getStringAttribute(rawExternalUser.getAttributes(), EMAIL);

        return this
                .isUserAuthorized(email, rawExternalUser.getToken())
                .map(userAuthorized ->
                        ExternalUser.builder()
                                .externalId(getStringAttribute(rawExternalUser.getAttributes(), EXTERNAL_ID))
                                .authSystem(ExternalAuthSystem.OAUTH_GITHUB)
                                .username(getStringAttribute(rawExternalUser.getAttributes(), USERNAME))
                                .displayName(getStringAttribute(rawExternalUser.getAttributes(), NAME))
                                .email(email)
                                .avatarUrl(getStringAttribute(rawExternalUser.getAttributes(), AVATAR_URL))
                                .authorized(userAuthorized)
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
     * Returns whether the current user is authorized to access the application.
     */
    private Mono<Boolean> isUserAuthorized(String email, String token) {
        final AppProperties.GitHubOauthOauth gitProperties = appProperties.getSecurity().getGithub();

        if (!gitProperties.isEmailAuthorized(email)) {
            return Mono.just(false);
        }

        if (!gitProperties.isOrganizationRestricted()) {
            return Mono.just(true);
        }

        return gitHubClient
                .findAllOrganizations(token)
                .collectList()
                .map(gitProperties::isOrganizationAuthorized);
    }
}
