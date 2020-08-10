package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.security.auth.external.RawExternalUser;
import be.sgerard.i18n.model.security.user.ExternalAuthSystem;
import be.sgerard.i18n.model.security.user.ExternalUser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

/**
 * {@link ExternalUserExtractor Extractor} of users coming from Google.
 *
 * @author Sebastien Gerard
 */
@Component
public class GoogleUserExtractor implements ExternalUserExtractor {

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

    public GoogleUserExtractor(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public boolean support(RawExternalUser externalUser) {
        return externalUser.getAuthSystem() == ExternalAuthSystem.OAUTH_GOOGLE;
    }

    @Override
    public Mono<ExternalUser> map(RawExternalUser rawExternalUser) {
        final String email = getStringAttribute(rawExternalUser.getAttributes(), EMAIL);

        return Mono.just(
                ExternalUser.builder()
                        .externalId(getStringAttribute(rawExternalUser.getAttributes(), EXTERNAL_ID))
                        .authSystem(ExternalAuthSystem.OAUTH_GOOGLE)
                        .username(email)
                        .displayName(getStringAttribute(rawExternalUser.getAttributes(), NAME))
                        .email(email)
                        .avatarUrl(getStringAttribute(rawExternalUser.getAttributes(), AVATAR_URL))
                        .authorized(isUserAuthorized(email))
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
     * Returns whether the current user is authorized to access the application.
     */
    private boolean isUserAuthorized(String email) {
        return appProperties.getSecurity().getGoogle().isEmailAuthorized(email);
    }
}
