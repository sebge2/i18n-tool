package be.sgerard.test.i18n.support;

import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Method;

/**
 * @author Sebastien Gerard
 */
public class TransactionInvocationInterceptor implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        final ApplicationContext applicationContext = SpringExtension.getApplicationContext(extensionContext);

        if (invocationContext.getExecutable().getAnnotation(TransactionalReactiveTest.class) != null) {
            final MongoOperations mongoOperations = applicationContext.getBean(MongoOperations.class);

            try {
                if (!mongoOperations.collectionExists(TranslationLocaleEntity.class)) {
                    mongoOperations.createCollection(TranslationLocaleEntity.class);
                }

                invocation.proceed();
            } finally {
                if (mongoOperations.collectionExists(TranslationLocaleEntity.class)) {
                    mongoOperations.dropCollection(TranslationLocaleEntity.class);
                }
            }
        } else {
            invocation.proceed();
        }
    }
}
