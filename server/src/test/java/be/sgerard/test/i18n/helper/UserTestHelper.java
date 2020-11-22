package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.user.dto.UserDto;
import be.sgerard.i18n.model.user.dto.UserPreferencesDto;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.test.StepVerifier;

/**
 * @author Sebastien Gerard
 */
@Component
public class UserTestHelper {

    private final WebTestClient webClient;
    private final UserManager userManager;

    public UserTestHelper(WebTestClient webClient, UserManager userManager) {
        this.webClient = webClient;
        this.userManager = userManager;
    }

    public StepUser createUser(InternalUserCreationDto creationDto) {
        return new StepUser(
                webClient.post()
                        .uri("/api/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(creationDto))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(UserDto.class)
                        .returnResult()
                        .getResponseBody()
        );
    }

    public StepCurrentUser currentUser() {
        return new StepCurrentUser(
                webClient.get().uri("/api/user/current")
                        .header(HttpHeaders.ACCEPT, "application/json")
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(UserDto.class)
                        .returnResult()
                        .getResponseBody()
        );
    }

    public UserTestHelper initAdminUser() {
        StepVerifier.create(userManager.initializeDefaultAdmin()).expectNextCount(1).verifyComplete();

        return this;
    }

    public class StepUser {

        private final UserDto user;

        public StepUser(UserDto user) {
            this.user = user;
        }

        public UserDto get() {
            return user;
        }

        public UserTestHelper and() {
            return UserTestHelper.this;
        }
    }

    public class StepCurrentUser extends StepUser {

        public StepCurrentUser(UserDto user) {
            super(user);
        }

        @SuppressWarnings("UnusedReturnValue")
        public StepCurrentUser resetPreferences() {
            webClient.put()
                    .uri("/api/user/current/preferences")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(UserPreferencesDto.builder().build()))
                    .exchange()
                    .expectStatus().isOk();

            return this;
        }
    }

}
