package be.sgerard.poc.githuboauth.service;

import be.sgerard.poc.githuboauth.configuration.AppProperties;
import org.springframework.stereotype.Service;

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
        this.timeoutInS = appProperties.getLockTimeoutInS();
    }

    @Override
    public <T> T executeInLock(Callable<T> runnable) throws Exception {
        if (lock.tryLock(timeoutInS, TimeUnit.SECONDS)) {
            try {
                return runnable.call();
            } finally {
                lock.unlock();
            }
        } else {
            throw new LockTimeoutException("Cannot obtain lock after " + timeoutInS + " second(s).");
        }
    }
}
