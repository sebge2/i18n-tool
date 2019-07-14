package be.sgerard.poc.githuboauth.service;

import java.util.concurrent.Callable;

/**
 * @author Sebastien Gerard
 */
public interface LockService {

    /**
     * @throws LockTimeoutException if the lock cannot be obtained
     * @throws Exception thrown by the runnable
     */
    <T> T executeInLock(Callable<T> runnable) throws Exception;
}
