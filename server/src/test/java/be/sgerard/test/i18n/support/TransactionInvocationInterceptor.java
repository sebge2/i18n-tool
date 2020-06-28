package be.sgerard.test.i18n.support;

import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author Sebastien Gerard
 */
public class TransactionInvocationInterceptor implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        if (!Objects.equals(getStore(extensionContext).get(TransactionInvocationInterceptor.class.getName()), "executed")
                && (invocationContext.getExecutable().getAnnotation(TransactionalReactiveTest.class) != null)) {
            try {
                intercept(invocation, extensionContext);
            } finally {
                final ApplicationContext applicationContext = SpringExtension.getApplicationContext(extensionContext);

                final MongoOperations mongoOperations = applicationContext.getBean(MongoOperations.class);

                dropIfNeeded(mongoOperations, TranslationLocaleEntity.class);
                dropIfNeeded(mongoOperations, UserEntity.class);
                dropIfNeeded(mongoOperations, RepositoryEntity.class);
                dropIfNeeded(mongoOperations, WorkspaceEntity.class);
            }
        } else {
            invocation.proceed();
        }

    }

    @Override
    public void interceptBeforeEachMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        if (extensionContext.getRequiredTestMethod().getAnnotation(TransactionalReactiveTest.class) != null) {
            getStore(extensionContext).put(TransactionInvocationInterceptor.class.getName(), "executed");

            intercept(invocation, extensionContext);
        } else {
            invocation.proceed();
        }
    }

    @Override
    public void interceptAfterEachMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        try {
            invocation.proceed();
        } finally {
            final ApplicationContext applicationContext = SpringExtension.getApplicationContext(extensionContext);

            final MongoOperations mongoOperations = applicationContext.getBean(MongoOperations.class);

            dropIfNeeded(mongoOperations, TranslationLocaleEntity.class);
            dropIfNeeded(mongoOperations, UserEntity.class);
            dropIfNeeded(mongoOperations, RepositoryEntity.class);
            dropIfNeeded(mongoOperations, WorkspaceEntity.class);
        }
    }

    private ExtensionContext.Store getStore(ExtensionContext extensionContext) {
        return extensionContext.getStore(ExtensionContext.Namespace.GLOBAL);
    }

    private void intercept(Invocation<Void> invocation, ExtensionContext extensionContext) throws Throwable {
        final ApplicationContext applicationContext = SpringExtension.getApplicationContext(extensionContext);

        final MongoOperations mongoOperations = applicationContext.getBean(MongoOperations.class);

        createIfNeeded(mongoOperations, TranslationLocaleEntity.class);
        createIfNeeded(mongoOperations, UserEntity.class);
        createIfNeeded(mongoOperations, RepositoryEntity.class);
        createIfNeeded(mongoOperations, WorkspaceEntity.class);

        invocation.proceed();
    }

    private void createIfNeeded(MongoOperations mongoOperations, Class<?> entityClass) {
        if (!mongoOperations.collectionExists(entityClass)) {
            mongoOperations.createCollection(entityClass);
        }
    }

    private void dropIfNeeded(MongoOperations mongoOperations, Class<?> entityClass) {
        if (mongoOperations.collectionExists(entityClass)) {
            if (entityClass.equals(UserEntity.class)) {
                mongoOperations.findAll(UserEntity.class).stream().filter(user -> !user.getUsername().equals(ADMIN_USER_NAME)).forEach(user -> mongoOperations.remove(user));
            } else {
                mongoOperations.dropCollection(entityClass);
            }
        }
    }
}
