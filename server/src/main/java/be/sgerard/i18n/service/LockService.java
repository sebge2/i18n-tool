package be.sgerard.i18n.service;

import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

/**
 * @author Sebastien Gerard
 */
public interface LockService {

    /**
     * @throws LockTimeoutException if the lock cannot be obtained
     */
    <T> Mono<T> executeInLock(Callable<Mono<T>> runnable) throws LockTimeoutException;
}
