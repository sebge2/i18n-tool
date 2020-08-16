package be.sgerard.i18n.service;

import be.sgerard.i18n.configuration.AppProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sebastien Gerard
 */
@Service
public class SimpleLockService implements LockService {

    private final Lock lock = new ReentrantLock();
    private final int timeoutInS;

    public SimpleLockService(AppProperties appProperties) {
        this.timeoutInS = appProperties.getLock().getTimeoutInS();
    }


    @Override
    public <T> Mono<T> executeAndGetMono(Callable<Mono<T>> runnable) throws LockTimeoutException {
        // TODO
        try {
            if (lock.tryLock(timeoutInS, TimeUnit.SECONDS)) {
                try {
                    return runnable.call();
                } finally {
                    lock.unlock();
                }
            } else {
                throw new LockTimeoutException("Cannot obtain lock after " + timeoutInS + " second(s).");
            }
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public <T> Flux<T> executeAndGetFlux(Callable<Flux<T>> runnable) throws LockTimeoutException {
        try {
            if (lock.tryLock(timeoutInS, TimeUnit.SECONDS)) {
                try {
                    return runnable.call();
                } finally {
                    lock.unlock();
                }
            } else {
                throw new LockTimeoutException("Cannot obtain lock after " + timeoutInS + " second(s).");
            }
        } catch (Exception e) {
            return Flux.error(e);
        }
    }
}
