package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.user.UserManager;
import be.sgerard.test.i18n.support.JsonHolderResultHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
@Component
public class UserTestHelper {

    private final MockMvc mockMvc;
    private final AsyncMockMvcTestHelper asyncMvc;
    private final ObjectMapper objectMapper;

    public UserTestHelper(MockMvc mockMvc,
                          AsyncMockMvcTestHelper asyncMvc,
                          ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.asyncMvc = asyncMvc;
        this.objectMapper = objectMapper;
    }

    public List<UserDto> findAllUsers() throws Exception {
        final JsonHolderResultHandler<List<UserDto>> resultHandler = new JsonHolderResultHandler<>(objectMapper, new TypeReference<List<UserDto>>() {
        });

        asyncMvc
                .perform(request(HttpMethod.GET, "/api/user/"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().is(OK.value()))
                .andDo(resultHandler);

        return resultHandler.getValue();
    }

    public UserDto getAdminUser() throws Exception {
        return findByUsernameOrDie(UserManager.ADMIN_USER_NAME);
    }

    public UserDto createUser(InternalUserCreationDto internalUserCreationDto) throws Exception {
        final JsonHolderResultHandler<UserDto> resultHandler = new JsonHolderResultHandler<>(objectMapper, UserDto.class);

        asyncMvc
                .perform(
                        post("/api/user/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(internalUserCreationDto))
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().is(OK.value()))
                .andDo(resultHandler);

        return resultHandler.getValue();
    }

    @SuppressWarnings("UnusedReturnValue")
    public UserTestHelper resetUserPreferences(String userName) throws Exception {
        final UserDto user = findByUsernameOrDie(userName);

        mockMvc
                .perform(
                        request(HttpMethod.PUT, "/api/user/" + user.getId() + "/preferences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(UserPreferencesDto.builder().build()))
                )
                .andExpect(status().is(OK.value()));

        return this;
    }

    private UserDto findByUsernameOrDie(String username) throws Exception {
        return findAllUsers().stream()
                .filter(user -> Objects.equals(user.getUsername(), username))
                .findFirst()
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(username));
    }
}
