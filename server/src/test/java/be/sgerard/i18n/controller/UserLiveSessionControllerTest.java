package be.sgerard.i18n.controller;

import be.sgerard.i18n.service.security.session.UserLiveSessionManager;
import be.sgerard.test.i18n.support.TransactionalReactiveTest;
import be.sgerard.test.i18n.support.WithJohnDoeSimpleUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.hamcrest.Matchers.hasSize;

/**
 * @author Sebastien Gerard
 */
public class UserLiveSessionControllerTest extends AbstractControllerTest {

    @Autowired
    private UserLiveSessionManager sessionManager;

    @Test
    @WithJohnDoeSimpleUser
    @TransactionalReactiveTest
    public void getCurrentLiveSessions() {
        StepVerifier
                .create(sessionManager.startSession())
                .expectNextCount(1)
                .expectComplete()
                .verify();

        webClient.get()
                .uri("/api/user-live-session/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").value(hasSize(1))
                .jsonPath("$[0].id").isNotEmpty()
                .jsonPath("$[0].userId").isNotEmpty()
                .jsonPath("$[0].userDisplayName").isEqualTo("John Doe");
    }
}
