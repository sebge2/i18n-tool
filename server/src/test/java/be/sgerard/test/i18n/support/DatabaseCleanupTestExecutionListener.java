package be.sgerard.test.i18n.support;

import be.sgerard.test.i18n.DatabaseTestHelper;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * @author Sebastien Gerard
 */
public class DatabaseCleanupTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) {
        if(testContext.getTestMethod().isAnnotationPresent(CleanupDatabase.class)){
            final DatabaseTestHelper databaseTestHelper = testContext.getApplicationContext().getBean(DatabaseTestHelper.class);

            databaseTestHelper.cleanup();
        }
    }

    @Override
    public int getOrder() {
        // WithSecurityContextTestExecutionListener = 10000
        return 10000 - 1;
    }
}
