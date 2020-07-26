package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.user.ExternalUser;
import be.sgerard.i18n.model.security.user.ExternalAuthSystem;
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

    public GoogleUserMapper() {
    }

    @Override
    public boolean support(OAuthExternalUser externalUser) {
        return externalUser.getOauthClient() == ExternalAuthSystem.OAUTH_GOOGLE;
    }

    @Override
    public Mono<ExternalUser> map(OAuthExternalUser externalUser) {
        return Mono.just(
                ExternalUser.builder()
                        .externalId(getStringAttribute(externalUser.getAttributes(), EXTERNAL_ID))
                        .authSystem(ExternalAuthSystem.OAUTH_GOOGLE)
                        .username(getStringAttribute(externalUser.getAttributes(), EMAIL))
                        .displayName(getStringAttribute(externalUser.getAttributes(), NAME))
                        .email(getStringAttribute(externalUser.getAttributes(), EMAIL))
                        .avatarUrl(getStringAttribute(externalUser.getAttributes(), AVATAR_URL))
                        .roles(UserRole.MEMBER_OF_ORGANIZATION)
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
}
