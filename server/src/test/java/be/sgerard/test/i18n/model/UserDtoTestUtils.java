package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;

/**
 * @author Sebastien Gerard
 */
public final class UserDtoTestUtils {

    public static final String JOHN_DOE_USERNAME = "john.doe";

    public static final String JOHN_DOE = "John Doe";

    public static final String JOHN_DOE_PASSWORD = "myPassword";

    public static final String JOHN_DOE_EMAIL = "john.doe@acme.com";

    public static final String JANE_DOE_USERNAME = "jane.doe";

    public static final String JANE_DOE = "Jane Doe";

    public static final String JANE_DOE_PASSWORD = "myPassword";

    public static final String JANE_DOE_EMAIL = "jane.doe@acme.com";

    public static final String GARRICK_KLEIN = "Garrick Klein";

    public static final String GARRICK_KLEIN_EMAIL = "garrick.klein@acme.com";

    public static final String GARRICK_KLEIN_TOKEN = "47b922c3-018f-41e1-b43c-47fa2776530e";

    public static UserDto.Builder userJohnDoe() {
        return UserDto.builder()
                .id("3290a30e-bf49-4e7e-9c94-d838b711f0ea")
                .username(JOHN_DOE_USERNAME)
                .email(JOHN_DOE_EMAIL)
                .type(UserDto.Type.INTERNAL);
    }

    public static InternalUserCreationDto.Builder userJohnDoeCreation() {
        return InternalUserCreationDto.builder(userJohnDoe().build())
                .password(JOHN_DOE_PASSWORD);
    }

}
