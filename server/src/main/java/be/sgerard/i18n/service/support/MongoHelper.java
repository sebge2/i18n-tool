package be.sgerard.i18n.service.support;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Helper for MongoDB.
 *
 * @author Sebastien Gerard
 */
@Component
public class MongoHelper {

    private final MongoConverter mongoConverter;
    private final MongoTemplate mongoTemplate;

    public MongoHelper(MongoConverter mongoConverter, MongoTemplate mongoTemplate) {
        this.mongoConverter = mongoConverter;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Returns all the document tpes.
     */
    public Collection<? extends Class<?>> getDocumentTypes() {
        return Optional
                .of(mongoConverter.getMappingContext())
                .filter(MongoMappingContext.class::isInstance)
                .map(context -> (MongoMappingContext) context)
                .map(mongoMappingContext ->
                        mongoMappingContext.getPersistentEntities()
                                .stream()
                                .filter(persistentEntity -> persistentEntity.getType().isAnnotationPresent(Document.class))
                                .map(BasicPersistentEntity::getType)
                                .collect(toList())
                )
                .orElseGet(Collections::emptyList);
    }

    /**
     * Setup indexes and creates document collections.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void setup() {
        final MappingContext<?, ?> mappingContext = mongoConverter.getMappingContext();

        if (mappingContext instanceof MongoMappingContext) {
            final MongoMappingContext mongoMappingContext = (MongoMappingContext) mappingContext;

            var resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
            getDocumentTypes()
                    .forEach(documentType -> {
                        if (!mongoTemplate.collectionExists(documentType)) {
                            mongoTemplate.createCollection(documentType);
                        }

                        final IndexOperations indexOps = mongoTemplate.indexOps(documentType);
                        resolver.resolveIndexFor(documentType).forEach(indexOps::ensureIndex);
                    });
        }
    }

    /**
     * Removes all documents from all collections.
     */
    public void cleanupAll() {
        getDocumentTypes()
                .forEach(documentType -> mongoTemplate.findAllAndRemove(new Query(), documentType));
    }
}
