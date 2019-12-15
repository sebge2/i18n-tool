package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.ToolLocale;
import be.sgerard.i18n.model.security.user.UserPreferencesDto;
import be.sgerard.i18n.service.user.UserManager;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
public class UserPreferencesControllerTest extends AbstractControllerTest {

    @Test
    @Transactional
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void getUserPreferences() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/api/user/" + userTestHelper.getAdminUser().getId() + "/preferences"))
                .andExpect(status().is(OK.value()));
    }

    @Test
    @Transactional
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateUserPreferences() throws Exception {
        try {
            mockMvc
                    .perform(
                            request(HttpMethod.PUT, "/api/user/" + userTestHelper.getAdminUser().getId() + "/preferences")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(UserPreferencesDto.builder().toolLocale(ToolLocale.FRENCH).build()))
                    )
                    .andExpect(status().is(OK.value()))
                    .andExpect(jsonPath("$.toolLocale").value(ToolLocale.FRENCH.name()));
        } finally {
            userTestHelper.resetUserPreferences(UserManager.ADMIN_USER_NAME);
        }
    }

}
