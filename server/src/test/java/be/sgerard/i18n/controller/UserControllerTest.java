package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.user.UserDto;
import be.sgerard.i18n.model.security.user.UserPatchDto;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.user.UserManager;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static be.sgerard.test.i18n.model.UserDtoTestUtils.userJohnDoeCreation;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
public class UserControllerTest extends AbstractControllerTest {

    @Test
    @Transactional
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findAllUsers() throws Exception {
        final UserDto johnDoe = userTestHelper.createUser(userJohnDoeCreation().build());

        mockMvc.perform(request(HttpMethod.GET, "/api/user"))
                .andExpect(status().is(OK.value()))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[?(@.username=='" + johnDoe.getUsername() + "')]").exists())
                .andExpect(jsonPath("$[?(@.username=='" + UserManager.ADMIN_USER_NAME + "')]").exists());
    }

    @Test
    @Transactional
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void getUserById() throws Exception {
        final UserDto johnDoe = userTestHelper.createUser(userJohnDoeCreation().build());

        mockMvc.perform(request(HttpMethod.GET, "/api/user/" + johnDoe.getId()))
                .andExpect(status().is(OK.value()))
                .andExpect(jsonPath("$.id").value(johnDoe.getId()))
                .andExpect(jsonPath("$.username").value(johnDoe.getUsername()))
                .andExpect(jsonPath("$.email").value(johnDoe.getEmail()))
                .andExpect(jsonPath("$.roles", containsInAnyOrder(johnDoe.getRoles().stream().map(UserRole::name).toArray())))
                .andExpect(jsonPath("$.type").value(johnDoe.getType().name()))
                .andExpect(jsonPath("$.avatarUrl").value(johnDoe.getAvatarUrl()));
    }

    @Test
    @Transactional
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateUser() throws Exception {
        final UserDto johnDoe = userTestHelper.createUser(userJohnDoeCreation().build());

        mockMvc
                .perform(
                        request(HttpMethod.PATCH, "/api/user/" + johnDoe.getId())
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
                .andExpect(status().is(OK.value()))
                .andExpect(jsonPath("$.id").value(johnDoe.getId()))
                .andExpect(jsonPath("$.username").value("stay_home"))
                .andExpect(jsonPath("$.email").value("john@localhost"))
                .andExpect(jsonPath("$.roles", containsInAnyOrder(UserRole.ADMIN.name())))
                .andExpect(jsonPath("$.type").value(johnDoe.getType().name()))
                .andExpect(jsonPath("$.avatarUrl").value("http://localhost/coro.png"));
    }

    @Test
    @Transactional
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateUserRoleNotAssignable() throws Exception {
        final UserDto johnDoe = userTestHelper.createUser(userJohnDoeCreation().build());

        mockMvc
                .perform(
                        request(HttpMethod.PATCH, "/api/user/" + johnDoe.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        UserPatchDto.builder()
                                                .roles(UserRole.MEMBER_OF_ORGANIZATION)
                                                .build()
                                ))
                )
                .andExpect(status().is(BAD_REQUEST.value()));
    }

    @Test
    @Transactional
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void deleteUser() throws Exception {
        final UserDto johnDoe = userTestHelper.createUser(userJohnDoeCreation().build());

        mockMvc.perform(request(HttpMethod.DELETE, "/api/user/" + johnDoe.getId()))
                .andExpect(status().is(NO_CONTENT.value()));

        mockMvc.perform(request(HttpMethod.GET, "/api/user/" + johnDoe.getId()))
                .andExpect(status().is(NOT_FOUND.value()));
    }

}
