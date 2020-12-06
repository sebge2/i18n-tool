package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.ToolLocale;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem;
import be.sgerard.i18n.model.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.user.persistence.InternalUserEntity;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

/**
 * @author Sebastien Gerard
 */
public final class UserEntityTestUtils {

    public static final String JOHN_DOE_ID = "3290a30e-bf49-4e7e-9c94-d838b711f0ea";
    public static final String JOHN_DOE_USERNAME = "john.doe";
    public static final String JOHN_DOE = "John Doe";
    public static final String JOHN_DOE_EMAIL = "john.doe@acme.com";
    public static final String JOHN_DOE_PASSWORD = "myPassword";

    public static final String JANE_DOE_ID = "cac5163d-9ad0-4506-9a44-5e54ff88e489";
    public static final String JANE_DOE_USERNAME = "jane.doe";
    public static final String JANE_DOE = "Jane Doe";
    public static final String JANE_DOE_EMAIL = "jane.doe@acme.com";
    public static final String JANE_DOE_PASSWORD = "myPassword";

    public static final String GARRICK_KLEIN_ID = "165586bd-16b6-4ae9-9e7a-275d70c03adc";
    public static final String GARRICK_KLEIN_EXT_ID = "139a5da0-7503-4114-97f5-d498a5becc5d";
    public static final String GARRICK_KLEIN_USERNAME = "garrick.klein";
    public static final String GARRICK_KLEIN = "Garrick Klein";
    public static final String GARRICK_KLEIN_EMAIL = "garrick.klein@acme.com";
    public static final String GARRICK_KLEIN_TOKEN = "47b922c3-018f-41e1-b43c-47fa2776530e";

    private UserEntityTestUtils() {
    }

    public static InternalUserEntity johnDoeUser() {
        final InternalUserEntity johnDoe = (InternalUserEntity) new InternalUserEntity(JOHN_DOE_USERNAME, JOHN_DOE)
                .setPassword(encode(JOHN_DOE_PASSWORD))
                .setAvatar(new byte[]{4,5,6})
                .setId(JOHN_DOE_ID)
                .setEmail(JANE_DOE_EMAIL)
                .setRoles(singleton(UserRole.MEMBER_OF_ORGANIZATION));

        johnDoe.getPreferences().setToolLocale(ToolLocale.ENGLISH);

        return johnDoe;
    }

    public static InternalUserEntity janeDoeUser() {
        final InternalUserEntity johnDoe = (InternalUserEntity) new InternalUserEntity(JANE_DOE_USERNAME, JANE_DOE)
                .setPassword(encode(JANE_DOE_PASSWORD))
                .setAvatar(new byte[]{1,2,3})
                .setId(JANE_DOE_ID)
                .setEmail(JANE_DOE_EMAIL)
                .setRoles(asList(UserRole.MEMBER_OF_ORGANIZATION, UserRole.ADMIN));

        johnDoe.getPreferences().setToolLocale(ToolLocale.ENGLISH);

        return johnDoe;
    }

    public static ExternalUserEntity garrickKleinUser() {
        final ExternalUserEntity garrickKlein = (ExternalUserEntity) new ExternalUserEntity(GARRICK_KLEIN_EXT_ID, ExternalAuthSystem.OAUTH_GITHUB)
                .setId(GARRICK_KLEIN_ID)
                .setRoles(asList(UserRole.MEMBER_OF_ORGANIZATION, UserRole.ADMIN))
                .setEmail(GARRICK_KLEIN_EMAIL)
                .setDisplayName(GARRICK_KLEIN);

        garrickKlein.getPreferences().setToolLocale(ToolLocale.ENGLISH);

        return garrickKlein;
    }

    private static String encode(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
