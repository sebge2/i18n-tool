package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;

/**
 * @author Sebastien Gerard
 */
public final class UserDtoTestUtils {

    public static final String JOHN_DOE = "john.doe";

    public static UserDto.Builder userJohnDoe() {
        return UserDto.builder()
                .id("3290a30e-bf49-4e7e-9c94-d838b711f0ea")
                .username(JOHN_DOE)
                .email("john.doe@acme.com")
                .type(UserDto.Type.INTERNAL)
                .avatarUrl("https://pickaface.net/gallery/avatar/unr_sample_161118_2054_ynlrg.png");
    }

    public static InternalUserCreationDto.Builder userJohnDoeCreation() {
        return InternalUserCreationDto.builder(userJohnDoe().build())
                .password("password");
    }

}
