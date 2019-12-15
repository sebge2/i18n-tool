package be.sgerard.i18n.service.security.session.repository;

import org.bson.Document;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.session.data.mongo.JdkMongoSessionConverter;
import org.springframework.session.data.mongo.MongoSession;
import org.springframework.session.data.mongo.ReactiveMongoSessionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * {@link ReactiveMongoSessionRepository Session repository} that uses MongoDB.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class SessionRepository extends ReactiveMongoSessionRepository {

    private final ReactiveMongoOperations mongoOperations;
    private final JdkMongoSessionConverter mongoSessionConverter;

    public SessionRepository(ReactiveMongoOperations mongoOperations) {
        super(mongoOperations);

        this.mongoOperations = mongoOperations;
        this.mongoSessionConverter = new JdkMongoSessionConverter(Duration.ofSeconds(this.getMaxInactiveIntervalInSeconds()));
        setMongoSessionConverter(this.mongoSessionConverter);
    }

    /**
     * Returns all the {@link MongoSession sessions}.
     */
    public Flux<MongoSession> findAll() {
        return this.mongoOperations
                .findAll(Document.class, getCollectionName())
                .map(this::convertToSession)
                .filter(mongoSession -> !mongoSession.isExpired());
    }

    /**
     * Converts the document to a session.
     */
    private MongoSession convertToSession(Document session) {
        return (MongoSession) mongoSessionConverter.convert(session, TypeDescriptor.valueOf(Document.class), TypeDescriptor.valueOf(MongoSession.class));
    }
}
