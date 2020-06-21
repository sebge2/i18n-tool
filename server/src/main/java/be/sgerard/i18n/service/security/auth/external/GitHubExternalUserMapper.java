package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.user.dto.ExternalUserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.json.JsonString;
import java.util.Map;
import java.util.Objects;

/**
 * {@link OAuthExternalUserMapper Mapper} for user coming from GitHub.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubExternalUserMapper implements OAuthExternalUserMapper {

    /**
     * OAuth attribute containing the username.
     */
    public static final String USERNAME = "login";

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

    /**
     * Name of the GitHub authentication system.
     *
     * @see OAuthExternalUser#getOauthClient()
     */
    public static final String GIT_HUB = "GitHub";

    public GitHubExternalUserMapper() {
    }

    @Override
    public boolean support(OAuthExternalUser externalUser) {
        return Objects.equals(externalUser.getOauthClient(), GIT_HUB);
    }

    @Override
    public Mono<ExternalUserDto> map(OAuthExternalUser externalUser) {
        // TODO check that it's part of the organization
        return Mono.just(
                ExternalUserDto.builder()
                        .externalId(getStringAttribute(externalUser.getAttributes(), EXTERNAL_ID))
                        .username(getStringAttribute(externalUser.getAttributes(), USERNAME))
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

//            // TODO
//    private boolean isRepoMember(String tokenValue) {
//        final RtGithub github = new RtGithub(tokenValue);
//
//        try {
//            return true;
////            return github.repos().get(new Coordinates.Simple(repository)).branches().iterate().iterator().hasNext();
//        } catch (AssertionError e) {
//            logger.error("error while connecting to "+ repository, e);
//            return false;
//        }
//    }
}
