package be.sgerard.i18n.service;

import be.sgerard.i18n.configuration.AppProperties;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.time.Duration;
import java.util.UUID;

import static be.sgerard.i18n.controller.support.RequestIdWebFilter.REQUEST_ID_CONTEXT_PARAM;

/**
 * @author Sebastien Gerard
 */
public class SimpleLockServiceTest {

    @Test
    public void executeAndGetMonoSingleCall() {
        final SimpleLockService service = createService(1000);
        StepVerifier
                .create(
                        executeFlux(service, "repository", 0, "value")
                                .subscriberContext(this::fillContext)
                )
                .expectNext("value")
                .verifyComplete();
    }

    @Test
    public void executeAndGetMonoNoTimeout() {
        final SimpleLockService service = createService(100);

        StepVerifier
                .create(
                        Flux
                                .merge(
                                        executeMono(service, "repository", 0, "value 1"),
                                        executeMono(service, "repository", 200, "value 2")
                                )
                                .publishOn(Schedulers.parallel())
                                .subscriberContext(this::fillContext)
                )
                .expectNext("value 1")
                .expectNext("value 2")
                .verifyComplete();
    }

    @Test
    public void executeAndGetMonoTimeoutReached() {
        final SimpleLockService service = createService(100);

        StepVerifier
                .create(
                        Flux
                                .merge(
                                        executeMono(service, "repository", 200, "value 1"),
                                        executeMono(service, "repository", 0, "value 2")
                                )
                                .publishOn(Schedulers.parallel())
                )
                .expectError(LockTimeoutException.class)
                .verify();
    }

    @Test
    public void executeAndGetMonoNoTimeoutSameRequest() {
        final SimpleLockService service = createService(100);

        StepVerifier
                .create(
                        Flux
                                .merge(
                                        executeMono(service, "repository", 200, "value 1"),
                                        executeMono(service, "repository", 0, "value 2")
                                )
                                .publishOn(Schedulers.parallel())
                                .subscriberContext(this::fillContext)
                )
                .expectNext("value 2")
                .expectNext("value 1")
                .verifyComplete();
    }

    @Test
    public void executeAndGetMonoTimeoutReachedForSecond() {
        final SimpleLockService service = createService(100);

        StepVerifier
                .create(
                        Flux
                                .merge(
                                        executeMono(service, "repository", 0, "value 1"),
                                        executeMono(service, "repository", 200, "value 2")
                                )
                                .publishOn(Schedulers.parallel())
                                .subscriberContext(this::fillContext)
                )
                .expectNext("value 1")
                .expectNext("value 2")
                .verifyComplete();
    }

    @Test
    public void executeAndGetMonoDifferentRepository() {
        final SimpleLockService service = createService(100);

        StepVerifier
                .create(
                        Flux
                                .merge(
                                        executeMono(service, "repository 1", 200, "value 1"),
                                        executeMono(service, "repository 2", 0, "value 2")
                                )
                                .publishOn(Schedulers.parallel())
                                .subscriberContext(this::fillContext)
                )
                .expectNext("value 2")
                .expectNext("value 1")
                .verifyComplete();
    }

    @Test
    public void executeAndGetFluxSingleCall() {
        final SimpleLockService service = createService(1000);
        StepVerifier
                .create(
                        executeFlux(service, "repository", 0, "value")
                                .subscriberContext(this::fillContext)
                )
                .expectNext("value")
                .verifyComplete();

    }

    @Test
    public void executeAndGetFluxNoTimeout() {
        final SimpleLockService service = createService(100);

        StepVerifier
                .create(
                        Flux
                                .merge(
                                        executeFlux(service, "repository", 0, "value 1"),
                                        executeFlux(service, "repository", 200, "value 2")
                                )
                                .publishOn(Schedulers.parallel())
                                .subscriberContext(this::fillContext)
                )
                .expectNext("value 1")
                .expectNext("value 2")
                .verifyComplete();
    }

    @Test
    public void executeAndGetFluxTimeoutReached() {
        final SimpleLockService service = createService(100);

        StepVerifier
                .create(
                        Flux
                                .merge(
                                        executeFlux(service, "repository", 200, "value 1"),
                                        executeFlux(service, "repository", 0, "value 2")
                                )
                                .publishOn(Schedulers.parallel())
                )
                .expectError(LockTimeoutException.class)
                .verify();
    }

    @Test
    public void executeAndGetFluxNoTimeoutSameRequest() {
        final SimpleLockService service = createService(100);

        StepVerifier
                .create(
                        Flux
                                .merge(
                                        executeFlux(service, "repository", 200, "value 1"),
                                        executeFlux(service, "repository", 0, "value 2")
                                )
                                .publishOn(Schedulers.parallel())
                                .subscriberContext(this::fillContext)
                )
                .expectNext("value 2")
                .expectNext("value 1")
                .verifyComplete();
    }

    @Test
    public void executeAndGetFluxTimeoutReachedForSecond() {
        final SimpleLockService service = createService(100);

        StepVerifier
                .create(
                        Flux
                                .merge(
                                        executeFlux(service, "repository", 0, "value 1"),
                                        executeFlux(service, "repository", 200, "value 2")
                                )
                                .publishOn(Schedulers.parallel())
                                .subscriberContext(this::fillContext)
                )
                .expectNext("value 1")
                .expectNext("value 2")
                .verifyComplete();
    }

    @Test
    public void executeAndGetFluxDifferentRepository() {
        final SimpleLockService service = createService(100);

        StepVerifier
                .create(
                        Flux
                                .merge(
                                        executeFlux(service, "repository 1", 200, "value 1"),
                                        executeFlux(service, "repository 2", 0, "value 2")
                                )
                                .publishOn(Schedulers.parallel())
                                .subscriberContext(this::fillContext)
                )
                .expectNext("value 2")
                .expectNext("value 1")
                .verifyComplete();
    }

    private SimpleLockService createService(int timeoutInMs) {
        return new SimpleLockService(new AppProperties().setLock(new AppProperties.Lock().setTimeoutInMS(timeoutInMs)));
    }

    private Mono<String> executeMono(SimpleLockService service, String repositoryId, int delay, String value) {
        return service.executeAndGetMono(repositoryId, () -> Mono.just(value).delayElement(Duration.ofMillis(delay)));
    }

    private Flux<String> executeFlux(SimpleLockService service, String repositoryId, int delay, String... values) {
        return service.executeAndGetFlux(repositoryId, () -> Flux.just(values).delayElements(Duration.ofMillis(delay)));
    }

    private Context fillContext(Context context) {
        return context.put(REQUEST_ID_CONTEXT_PARAM, UUID.randomUUID().toString());
    }

}
