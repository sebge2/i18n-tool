package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.Objects;

/**
 * @author Sebastien Gerard
 */
@Component
public class UserTestHelper {

    private final WebTestClient webClient;

    public UserTestHelper(WebTestClient webClient) {
        this.webClient = webClient;
    }

    public List<UserDto> findAllUsers() {
        return webClient.get().uri("/api/user/")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .returnResult()
                .getResponseBody();
    }

    public UserDto getAdminUser() {
        return findByUsernameOrDie(UserManager.ADMIN_USER_NAME);
    }

    public UserDto createUser(InternalUserCreationDto internalUserCreationDto) {
        return webClient.post()
                .uri("/api/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(internalUserCreationDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserDto.class)
                .returnResult()
                .getResponseBody();
    }

    @SuppressWarnings("UnusedReturnValue")
    public UserTestHelper resetUserPreferences(String userName) {
        final UserDto user = findByUsernameOrDie(userName);

        webClient.put()
                .uri("/api/user/{id}/preferences", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(UserPreferencesDto.builder().build()))
                .exchange()
                .expectStatus().isOk();

        return this;
    }

    private UserDto findByUsernameOrDie(String username) {
        return findAllUsers().stream()
                .filter(user -> Objects.equals(user.getUsername(), username))
                .findFirst()
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(username));
    }
}
