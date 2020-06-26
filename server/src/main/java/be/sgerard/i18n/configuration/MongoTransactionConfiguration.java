package be.sgerard.i18n.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for MongoDB.
 *
 * @author Sebastien Gerard
 */
@Configuration
@EnableReactiveMongoRepositories(basePackages = "be.sgerard.i18n")
@EnableTransactionManagement
public class MongoTransactionConfiguration  {

    @Bean
    public ReactiveTransactionManager transactionManager(ReactiveMongoDatabaseFactory da) {
        return new ReactiveMongoTransactionManager(da);
    }

}
