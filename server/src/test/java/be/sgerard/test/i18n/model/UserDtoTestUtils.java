package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;

import static be.sgerard.test.i18n.model.UserEntityTestUtils.johnDoeUser;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
public final class UserDtoTestUtils {

    public static UserDto.Builder johnDoeUserDto() {
        return UserDto.builder(johnDoeUser());
    }

    public static InternalUserCreationDto.Builder userJohnDoeCreation() {
        final UserDto userDto = johnDoeUserDto().build();

        return InternalUserCreationDto.builder()
                .username(userDto.getUsername())
                .displayName(userDto.getDisplayName())
                .email(userDto.getEmail())
                .roles(userDto.getRoles().stream().filter(UserRole::isAssignableByEndUser).collect(toList()))
                .password(johnDoeUser().getPassword());
    }

}
