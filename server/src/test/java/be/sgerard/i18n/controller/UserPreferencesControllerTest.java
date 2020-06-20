package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.ToolLocale;
import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.i18n.service.user.UserManager;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        asyncMvc
                .perform(get("/api/user/{id}/preferences", user.getAdminUser().getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().is(OK.value()));
    }

    @Test
    @Transactional
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateUserPreferences() throws Exception {
        try {
            asyncMvc
                    .perform(
                            put("/api/user/{id}/preferences", user.getAdminUser().getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(UserPreferencesDto.builder().toolLocale(ToolLocale.FRENCH).build()))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().is(OK.value()))
                    .andExpect(jsonPath("$.toolLocale").value(ToolLocale.FRENCH.name()));
        } finally {
            user.resetUserPreferences(UserManager.ADMIN_USER_NAME);
        }
    }

}
