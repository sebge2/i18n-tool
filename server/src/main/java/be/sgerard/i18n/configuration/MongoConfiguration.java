package be.sgerard.i18n.configuration;

import be.sgerard.i18n.model.support.PatternToStringConverter;
import be.sgerard.i18n.model.support.StringToPatternConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static java.util.Arrays.asList;

/**
 * Configuration for MongoDB.
 *
 * @author Sebastien Gerard
 */
@Configuration
@EnableReactiveMongoRepositories(basePackages = "be.sgerard.i18n")
@EnableTransactionManagement
public class MongoConfiguration {

    private final MongoConverter mongoConverter;
    private final MongoTemplate mongoTemplate;

    @Lazy
    public MongoConfiguration(MongoConverter mongoConverter, MongoTemplate mongoTemplate) {
        this.mongoConverter = mongoConverter;
        this.mongoTemplate = mongoTemplate;
    }

    @Bean
    public ReactiveTransactionManager transactionManager(ReactiveMongoDatabaseFactory da) {
        return new ReactiveMongoTransactionManager(da);
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(asList(
                new PatternToStringConverter(),
                new StringToPatternConverter()
        ));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {
        final MappingContext<?, ?> mappingContext = mongoConverter.getMappingContext();

        if (mappingContext instanceof MongoMappingContext) {
            MongoMappingContext mongoMappingContext = (MongoMappingContext) mappingContext;
            for (BasicMongoPersistentEntity<?> persistentEntity : mongoMappingContext.getPersistentEntities()) {
                var clazz = persistentEntity.getType();
                if (clazz.isAnnotationPresent(Document.class)) {
                    var resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);

                    final IndexOperations indexOps = mongoTemplate.indexOps(clazz);
                    resolver.resolveIndexFor(clazz).forEach(indexOps::ensureIndex);
                }
            }
        }
    }
}
