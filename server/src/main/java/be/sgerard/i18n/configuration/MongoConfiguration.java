package be.sgerard.i18n.configuration;

import be.sgerard.i18n.model.support.LocalizedStringToMapConverter;
import be.sgerard.i18n.model.support.MapToLocalizedStringConverter;
import be.sgerard.i18n.model.support.PatternToStringConverter;
import be.sgerard.i18n.model.support.StringToPatternConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
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

    public MongoConfiguration() {
    }

    @Bean
    public ReactiveTransactionManager transactionManager(ReactiveMongoDatabaseFactory da) {
        return new ReactiveMongoTransactionManager(da);
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(asList(
                new PatternToStringConverter(),
                new StringToPatternConverter(),
                new LocalizedStringToMapConverter(),
                new MapToLocalizedStringConverter()
        ));
    }
}
