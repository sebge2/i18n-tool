package be.sgerard.test.i18n;

import be.sgerard.i18n.service.support.MongoHelper;
import org.springframework.stereotype.Component;

/**
 * @author Sebastien Gerard
 */
@Component
public class DatabaseTestHelper {

    private final MongoHelper mongoHelper;

    public DatabaseTestHelper(MongoHelper mongoHelper) {
        this.mongoHelper = mongoHelper;
    }

    public DatabaseTestHelper cleanup() {
        mongoHelper.cleanupAll();
        return this;
    }
}
