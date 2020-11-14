package be.sgerard.i18n.service;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.controller.support.RequestIdWebFilter;
import lombok.ToString;
import lombok.Value;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;


/**
 * Implementation of the {@link LockService lock service} executed in a single instance.
 *
 * @author Sebastien Gerard
 */
@Service
public class SimpleLockService implements LockService {

    private static final Logger logger = LoggerFactory.getLogger(SimpleLockService.class);

    private final int timeoutInMS;
    private final List<Task> tasks = Collections.synchronizedList(new ArrayList<>());
    private final EmitterProcessor<Task> emitter;
    private final FluxSink<Task> sink;

    public SimpleLockService(AppProperties appProperties) {
        this.timeoutInMS = appProperties.getLock().getTimeoutInMS();
        this.emitter = EmitterProcessor.create(false);
        this.sink = emitter.sink(FluxSink.OverflowStrategy.DROP);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Mono<T> executeAndGetMono(String repositoryId, Supplier<Mono<T>> supplier) throws LockTimeoutException {
        return createMonoTask(repositoryId, (Supplier<Mono<Object>>) (Object) supplier)
                .flatMap(task ->
                        acquireLockFor(task)
                                .flatMap(Task::getMono)
                                .map(mono -> (T) mono)
                                .doOnNext(value -> logger.trace("Result of task [{}] is: [{}].", task, value))
                                .doFinally(signalType -> doFinally(task))
                );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Flux<T> executeAndGetFlux(String repositoryId, Supplier<Flux<T>> supplier) throws LockTimeoutException {
        return createFluxTask(repositoryId, (Supplier<Flux<Object>>) (Object) supplier)
                .flatMapMany(task ->
                        acquireLockFor(task)
                                .flatMapMany(Task::getFlux)
                                .map(value -> (T) value)
                                .doOnNext(value -> logger.trace("Result of task [{}] is: [{}].", task, value))
                                .doFinally(signalType -> doFinally(task))
                );
    }

    /**
     * Creates a task for a mono.
     */
    private Mono<Task> createMonoTask(String repositoryId, Supplier<Mono<Object>> supplier) {
        return Mono.deferWithContext(context -> Mono.just(new Task(repositoryId, supplier, RequestIdWebFilter.getRequestId(context).orElse(null))));
    }

    /**
     * Creates a task for a flux.
     */
    private Mono<Task> createFluxTask(String repositoryId, Supplier<Flux<Object>> supplier) {
        return Mono.deferWithContext(context -> Mono.just(new Task(repositoryId, supplier, RequestIdWebFilter.getRequestId(context).orElse(null))));
    }

    /**
     * Acquires the lock for the specified {@link Task task} or throws an exception if it cannot be obtained.
     */
    private Mono<Task> acquireLockFor(Task task) {
        tasks.add(task);
        sink.next(task);

        logger.trace("Add new task [{}].", task);

        return emitter
                .flatMap(updatedTask -> checkCanBeStarted(task))
                .timeout(Duration.ofMillis(timeoutInMS), Mono.defer(() -> Mono.error(new LockTimeoutException(timeoutInMS))))
                .doOnNext(updatedTask -> logger.trace("Start of task [{}].", task))
                .next();
    }

    /**
     * Checks whether the specified task can be started, otherwise returns empty.
     */
    private Mono<Task> checkCanBeStarted(Task task) {
        for (Task taskInQueue : tasks.toArray(new Task[0])) {
            if (Objects.equals(taskInQueue, task) || task.hasSameRequestId(taskInQueue)) {
                return Mono.just(task);
            } else if (Objects.equals(taskInQueue.getRepository(), task.getRepository())) {
                return Mono.empty();
            }
        }

        return Mono.empty();
    }

    /**
     * Executes the cleanup after that the specified task has been executed/failed.
     */
    private void doFinally(Task task) {
        tasks.remove(task);

        logger.trace("End of task [{}], there are {} pending tasks.", task, tasks.size());

        sink.next(task);
    }

    /**
     * A task to be executed.
     */
    @Value
    @SuppressWarnings("RedundantModifiersValueLombok")
    private static class Task {

        private final String id = UUID.randomUUID().toString();

        private String repository;

        @ToString.Exclude
        private Supplier<? extends Publisher<Object>> supplier;

        private String currentRequestId;

        public Mono<Object> getMono() {
            return (Mono<Object>) getSupplier().get();
        }

        public Flux<Object> getFlux() {
            return (Flux<Object>) getSupplier().get();
        }

        /**
         * Returns whether the current task and the specified task have a {@link #getCurrentRequestId() request id}
         * and if they are the same.
         */
        public boolean hasSameRequestId(Task other) {
            return (getCurrentRequestId() != null)
                    && (other.getCurrentRequestId() != null)
                    && Objects.equals(getCurrentRequestId(), other.getCurrentRequestId());
        }
    }
}
