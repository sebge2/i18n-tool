package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.user.dto.CurrentUserPasswordUpdateDto;
import be.sgerard.i18n.model.user.dto.CurrentUserPatchDto;
import be.sgerard.i18n.model.user.dto.UserDto;
import be.sgerard.i18n.model.user.dto.UserPatchDto;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.test.i18n.model.UserEntityTestUtils;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import be.sgerard.test.i18n.support.auth.internal.WithJohnDoeSimpleUser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;

import static be.sgerard.i18n.service.user.UserManagerImpl.ADMIN_AVATAR;
import static be.sgerard.test.i18n.model.UserDtoTestUtils.userJohnDoeCreation;
import static be.sgerard.test.i18n.model.UserEntityTestUtils.JANE_DOE_USERNAME;
import static org.hamcrest.Matchers.*;

/**
 * @author Sebastien Gerard
 */
public class UserControllerTest extends AbstractControllerTest {

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void findAllUsers() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build()).get();

        webClient
                .get()
                .uri("/api/user")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").value(hasSize(greaterThanOrEqualTo(2)))
                .jsonPath("$[?(@.username=='" + johnDoe.getUsername() + "')]").exists()
                .jsonPath("$[?(@.username=='" + JANE_DOE_USERNAME + "')]").exists();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void getUserById() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build()).get();

        webClient
                .get()
                .uri("/api/user/{id}", johnDoe.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(johnDoe.getId())
                .jsonPath("$.username").isEqualTo(johnDoe.getUsername())
                .jsonPath("$.email").isEqualTo(johnDoe.getEmail())
                .jsonPath("$.roles").value(containsInAnyOrder(johnDoe.getRoles().stream().map(UserRole::name).toArray()))
                .jsonPath("$.type").isEqualTo(johnDoe.getType().name());
    }

    @Test
    @CleanupDatabase
    @WithJohnDoeSimpleUser
    public void getCurrentUser() {
        webClient
                .get()
                .uri("/api/user/current")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.username").isEqualTo(UserEntityTestUtils.JOHN_DOE_USERNAME)
                .jsonPath("$.email").isEqualTo(UserEntityTestUtils.JOHN_DOE_EMAIL)
                .jsonPath("$.roles").value(containsInAnyOrder(UserRole.MEMBER_OF_ORGANIZATION.name()))
                .jsonPath("$.type").isEqualTo(UserDto.Type.INTERNAL.name());
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void createSameUserName() {
        user.createUser(userJohnDoeCreation().build()).get();

        webClient
                .post()
                .uri("/api/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userJohnDoeCreation().build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.messages[0]").isEqualTo("The username [john.doe] is already used.");
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void updateUser() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build()).get();

        webClient
                .patch()
                .uri("/api/user/{id}", johnDoe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        UserPatchDto.builder()
                                .username("stay_home")
                                .email("john@localhost")
                                .roles(UserRole.ADMIN)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(johnDoe.getId())
                .jsonPath("$.username").isEqualTo("stay_home")
                .jsonPath("$.email").isEqualTo("john@localhost")
                .jsonPath("$.roles").value(containsInAnyOrder(UserRole.ADMIN.name(), UserRole.MEMBER_OF_ORGANIZATION.name()))
                .jsonPath("$.type").isEqualTo(johnDoe.getType().name());
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void updateUserUsernameExists() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build()).get();

        webClient
                .patch()
                .uri("/api/user/{id}", johnDoe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        UserPatchDto.builder()
                                .username(JANE_DOE_USERNAME)
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.messages[0]").isEqualTo("The username [jane.doe] is already used.");
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void updateUserRoleNotAssignable() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build()).get();

        webClient
                .patch()
                .uri("/api/user/{id}", johnDoe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        UserPatchDto.builder()
                                .roles(UserRole.MEMBER_OF_ORGANIZATION)
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.messages[0]").isEqualTo("The role MEMBER_OF_ORGANIZATION cannot be assigned.");
    }

    @Test
    @CleanupDatabase
    @WithJohnDoeSimpleUser
    public void updateCurrentUser() {
        webClient
                .patch()
                .uri("/api/user/current")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        CurrentUserPatchDto.builder()
                                .username("john.doe.update")
                                .displayName("John Doe Updated")
                                .email("john.doe@warner.com")
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("john.doe.update")
                .jsonPath("$.displayName").isEqualTo("John Doe Updated")
                .jsonPath("$.email").isEqualTo("john.doe@warner.com");
    }

    // TODO update current user admin

    @Test
    @CleanupDatabase
    @WithJohnDoeSimpleUser
    public void updateCurrentUserPassword() {
        webClient
                .put()
                .uri("/api/user/current/password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        new CurrentUserPasswordUpdateDto(UserEntityTestUtils.JOHN_DOE_PASSWORD, "123")
                )
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @CleanupDatabase
    @WithJohnDoeSimpleUser
    public void updateCurrentUserPasswordWrongPassword() {
        webClient
                .put()
                .uri("/api/user/current/password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        new CurrentUserPasswordUpdateDto("wrong-password", "123")
                )
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.messages[0]").isEqualTo("The password does not match the existing one.");
    }

    @Test
    @CleanupDatabase
    @WithJohnDoeSimpleUser
    public void updateCurrentUserAvatar() throws IOException {
        webClient
                .put()
                .uri("/api/user/current/avatar")
                .contentType(MediaType.IMAGE_JPEG)
                .bodyValue(IOUtils.toByteArray(UserControllerTest.class.getResourceAsStream(ADMIN_AVATAR)))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void getUserAvatar() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build()).get();

        webClient
                .get()
                .uri("/api/user/{id}/avatar", johnDoe.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.IMAGE_PNG)
                .expectBody();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void deleteUser() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build()).get();

        webClient
                .delete()
                .uri("/api/user/{id}", johnDoe.getId())
                .exchange()
                .expectStatus().isNoContent();

        webClient
                .get()
                .uri("/api/user/{id}", johnDoe.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @CleanupDatabase
    @WithJohnDoeSimpleUser
    public void deleteUserNotAllowed() {
        final String currentUserId = user.currentUser().get().getId();

        webClient
                .delete()
                .uri("/api/user/{id}", currentUserId)
                .exchange()
                .expectStatus().isForbidden();
    }

}
