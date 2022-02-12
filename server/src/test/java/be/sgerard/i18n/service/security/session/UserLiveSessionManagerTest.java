package be.sgerard.i18n.service.security.session;

import be.sgerard.i18n.AbstractIntegrationTest;
import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJohnDoeSimpleUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Sebastien Gerard
 */
public class UserLiveSessionManagerTest extends AbstractIntegrationTest {

    @Autowired
    private UserLiveSessionManager sessionManager;

    @Test
    @WithJohnDoeSimpleUser
    @CleanupDatabase
    public void startSession() {
        StepVerifier
                .create(sessionManager.startSession())
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    @WithJohnDoeSimpleUser
    @CleanupDatabase
    public void getCurrentLiveSessions() {
        StepVerifier
                .create(sessionManager.startSession())
                .expectNextCount(1)
                .expectComplete()
                .verify();

        StepVerifier
                .create(sessionManager.getCurrentLiveSessions())
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    @WithJohnDoeSimpleUser
    @CleanupDatabase
    public void getSessionOrDie() {
        final UserLiveSessionEntity session = createSession();

        StepVerifier
                .create(sessionManager.getSessionOrDie(session.getId()))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    @WithJohnDoeSimpleUser
    @CleanupDatabase
    public void getSessionOrDieFailed() {
        StepVerifier
                .create(sessionManager.getSessionOrDie("unknown"))
                .expectError()
                .verify();
    }

    @Test
    @WithJohnDoeSimpleUser
    @CleanupDatabase
    public void stopSession() {
        final UserLiveSessionEntity session = createSession();

        StepVerifier
                .create(sessionManager.stopSession(session))
                .expectComplete()
                .verify();

        StepVerifier
                .create(sessionManager.getSessionOrDie(session.getId()))
                .expectNextCount(1)
                .expectComplete()
                .verify();

        StepVerifier
                .create(sessionManager.getCurrentLiveSessions())
                .expectComplete()
                .verify();
    }

    @Test
    @WithJohnDoeSimpleUser
    @CleanupDatabase
    public void deleteSession() {
        final UserLiveSessionEntity session = createSession();

        StepVerifier
                .create(sessionManager.deleteSession(session))
                .expectComplete()
                .verify();

        StepVerifier
                .create(sessionManager.getSessionOrDie(session.getId()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    @WithJohnDoeSimpleUser
    @CleanupDatabase
    public void deleteAll() {
        final UserLiveSessionEntity session = createSession();

        StepVerifier
                .create(sessionManager.deleteAll(session.getUser()))
                .expectComplete()
                .verify();

        StepVerifier
                .create(sessionManager.getSessionOrDie(session.getId()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    private UserLiveSessionEntity createSession() {
        final Set<UserLiveSessionEntity> sessions = new HashSet<>();

        StepVerifier
                .create(sessionManager.startSession())
                .recordWith(() -> sessions)
                .expectNextCount(1)
                .expectComplete()
                .verify();

        return sessions.iterator().next();
    }

}
