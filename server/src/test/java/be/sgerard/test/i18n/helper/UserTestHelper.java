package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

/**
 * @author Sebastien Gerard
 */
@Component
public class UserTestHelper {

    private final WebTestClient webClient;

    public UserTestHelper(WebTestClient webClient) {
        this.webClient = webClient;
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
    public UserTestHelper resetUserPreferences() {
        webClient.put()
                .uri("/api/user/current/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(UserPreferencesDto.builder().build()))
                .exchange()
                .expectStatus().isOk();

        return this;
    }

    public UserDto getCurrentUser() {
        return webClient.get().uri("/api/user/current")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .returnResult()
                .getResponseBody();
    }
}
