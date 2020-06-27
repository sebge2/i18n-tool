package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.user.UserManager;
import be.sgerard.test.i18n.support.TransactionalReactiveTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static be.sgerard.test.i18n.model.UserDtoTestUtils.userJohnDoeCreation;
import static org.hamcrest.Matchers.*;

/**
 * @author Sebastien Gerard
 */
public class UserControllerTest extends AbstractControllerTest {

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findAllUsers() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build());

        webClient
                .get()
                .uri("/api/user")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").value(hasSize(greaterThanOrEqualTo(2)))
                .jsonPath("$[?(@.username=='" + johnDoe.getUsername() + "')]").exists()
                .jsonPath("$[?(@.username=='" + UserManager.ADMIN_USER_NAME + "')]").exists();
    }

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void getUserById() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build());

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
                .jsonPath("$.type").isEqualTo(johnDoe.getType().name())
                .jsonPath("$.avatarUrl").isEqualTo(johnDoe.getAvatarUrl());
    }

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateUser() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build());

        webClient
                .patch()
                .uri("/api/user/{id}", johnDoe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        UserPatchDto.builder()
                                .username("stay_home")
                                .email("john@localhost")
                                .avatarUrl("http://localhost/coro.png")
                                .roles(UserRole.ADMIN)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(johnDoe.getId())
                .jsonPath("$.username").isEqualTo("stay_home")
                .jsonPath("$.email").isEqualTo("john@localhost")
                .jsonPath("$.roles").value(containsInAnyOrder(UserRole.ADMIN.name()))
                .jsonPath("$.type").isEqualTo(johnDoe.getType().name())
                .jsonPath("$.avatarUrl").isEqualTo("http://localhost/coro.png");
    }

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateUserRoleNotAssignable() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build());

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
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void deleteUser() {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build());

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

}
