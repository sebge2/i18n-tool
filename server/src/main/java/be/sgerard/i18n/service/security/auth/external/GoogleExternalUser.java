package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.user.dto.ExternalUserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

/**
 * {@link OAuthExternalUserMapper Mapper} for user coming from Google.
 *
 * @author Sebastien Gerard
 */
@Component
public class GoogleExternalUser implements OAuthExternalUserMapper {

    /**
     * OAuth attribute containing the email.
     */
    public static final String EMAIL = "email";

    /**
     * OAuth attribute containing the avatar URL.
     */
    public static final String AVATAR_URL = "picture";

    /**
     * OAuth attribute containing the external ID.
     */
    public static final String EXTERNAL_ID = "sub";

    /**
     * Name of the Google authentication system.
     *
     * @see OAuthExternalUser#getOauthClient()
     */
    public static final String GOOGLE = "Google";

    public GoogleExternalUser() {
    }

    @Override
    public boolean support(OAuthExternalUser externalUser) {
        return Objects.equals(externalUser.getOauthClient(), GOOGLE);
    }

    @Override
    public Mono<ExternalUserDto> map(OAuthExternalUser externalUser) {
        return Mono.just(
                ExternalUserDto.builder()
                        .externalId(getStringAttribute(externalUser.getAttributes(), EXTERNAL_ID))
                        .username(getStringAttribute(externalUser.getAttributes(), EMAIL))
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
