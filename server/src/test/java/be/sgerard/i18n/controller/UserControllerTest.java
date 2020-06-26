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
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
public class UserControllerTest extends AbstractControllerTest {

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findAllUsers() throws Exception {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build());

        asyncMvc
                .perform(get("/api/user"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().is(OK.value()))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[?(@.username=='" + johnDoe.getUsername() + "')]").exists())
                .andExpect(jsonPath("$[?(@.username=='" + UserManager.ADMIN_USER_NAME + "')]").exists());
    }

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void getUserById() throws Exception {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build());

        asyncMvc
                .perform(get("/api/user/{id}", johnDoe.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().is(OK.value()))
                .andExpect(jsonPath("$.id").value(johnDoe.getId()))
                .andExpect(jsonPath("$.username").value(johnDoe.getUsername()))
                .andExpect(jsonPath("$.email").value(johnDoe.getEmail()))
                .andExpect(jsonPath("$.roles", containsInAnyOrder(johnDoe.getRoles().stream().map(UserRole::name).toArray())))
                .andExpect(jsonPath("$.type").value(johnDoe.getType().name()))
                .andExpect(jsonPath("$.avatarUrl").value(johnDoe.getAvatarUrl()));
    }

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateUser() throws Exception {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build());

        asyncMvc
                .perform(
                        patch("/api/user/{id}", johnDoe.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        UserPatchDto.builder()
                                                .username("stay_home")
                                                .email("john@localhost")
                                                .avatarUrl("http://localhost/coro.png")
                                                .roles(UserRole.ADMIN)
                                                .build()
                                ))
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().is(OK.value()))
                .andExpect(jsonPath("$.id").value(johnDoe.getId()))
                .andExpect(jsonPath("$.username").value("stay_home"))
                .andExpect(jsonPath("$.email").value("john@localhost"))
                .andExpect(jsonPath("$.roles", containsInAnyOrder(UserRole.ADMIN.name())))
                .andExpect(jsonPath("$.type").value(johnDoe.getType().name()))
                .andExpect(jsonPath("$.avatarUrl").value("http://localhost/coro.png"));
    }

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateUserRoleNotAssignable() throws Exception {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build());

        asyncMvc
                .perform(
                        patch("/api/user/{id}", johnDoe.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        UserPatchDto.builder()
                                                .roles(UserRole.MEMBER_OF_ORGANIZATION)
                                                .build()
                                ))
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().is(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.messages[0]").value("The role MEMBER_OF_ORGANIZATION cannot be assigned."));
    }

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void deleteUser() throws Exception {
        final UserDto johnDoe = user.createUser(userJohnDoeCreation().build());

        asyncMvc
                .perform(delete("/api/user/{id}", johnDoe.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().is(NO_CONTENT.value()));

        asyncMvc
                .perform(get("/api/user/{id}", johnDoe.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().is(NOT_FOUND.value()));
    }

}
