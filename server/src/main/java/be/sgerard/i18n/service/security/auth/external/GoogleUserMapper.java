package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.user.ExternalAuthSystem;
import be.sgerard.i18n.model.security.user.ExternalUser;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

/**
 * {@link OAuthUserMapper Mapper} for user coming from Google.
 *
 * @author Sebastien Gerard
 */
@Component
public class GoogleUserMapper implements OAuthUserMapper {

    /**
     * OAuth attribute containing the email.
     */
    public static final String EMAIL = "email";

    /**
     * OAuth attribute containing the name.
     */
    public static final String NAME = "name";

    /**
     * OAuth attribute containing the avatar URL.
     */
    public static final String AVATAR_URL = "picture";

    /**
     * OAuth attribute containing the external ID.
     */
    public static final String EXTERNAL_ID = "sub";

    private final AppProperties appProperties;

    public GoogleUserMapper(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public boolean support(OAuthExternalUser externalUser) {
        return externalUser.getOauthClient() == ExternalAuthSystem.OAUTH_GOOGLE;
    }

    @Override
    public Mono<ExternalUser> map(OAuthExternalUser oAuthExternalUser) {
        final String email = getStringAttribute(oAuthExternalUser.getAttributes(), EMAIL);

        return Mono.just(
                ExternalUser.builder()
                        .externalId(getStringAttribute(oAuthExternalUser.getAttributes(), EXTERNAL_ID))
                        .authSystem(ExternalAuthSystem.OAUTH_GOOGLE)
                        .username(email)
                        .displayName(getStringAttribute(oAuthExternalUser.getAttributes(), NAME))
                        .email(email)
                        .avatarUrl(getStringAttribute(oAuthExternalUser.getAttributes(), AVATAR_URL))
                        .roles(isUserAllowed(email) ? new UserRole[]{UserRole.MEMBER_OF_ORGANIZATION} : new UserRole[0])
                        .build()
        );
    }

    /**
     * Returns the string from the OAuth user's attributes.
     */
    private String getStringAttribute(Map<String, ?> attributes, String key) {
        if (attributes.containsKey(key)) {
            return Objects.toString(attributes.get(key));
        } else {
            return null;
        }
    }

    /**
     * Returns whether the current user is allowed to access the application.
     */
    private boolean isUserAllowed(String email) {
        return appProperties.getSecurity().getGoogle().isEmailAllowed(email);
    }
}
