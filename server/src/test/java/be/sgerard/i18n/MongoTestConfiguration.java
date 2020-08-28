package be.sgerard.i18n;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.bson.Document;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;

/**
 * @author Sebastien Gerard
 */
@Configuration
public class MongoTestConfiguration {

    private final int serverPort;

    public MongoTestConfiguration() {
        serverPort = 37436 /*Network.getFreeServerPort()*/;

        System.out.println("-------------------------");
        System.out.println("Mongo will be available on " + serverPort);
        System.out.println("-------------------------");
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MongodExecutable mongodExecutable(IMongodConfig mongodConfig) {
        final MongodStarter starter = MongodStarter.getDefaultInstance();

        new Thread(() -> {
            final MongoClient client = new MongoClient("localhost", serverPort);
            client.getDatabase("admin").runCommand(new Document("replSetInitiate", new Document()));
        }).start();

        return starter.prepare(mongodConfig);
    }

    @Bean
    public IMongodConfig mongoConfig() throws Exception {
        final IMongoCmdOptions cmdOptions = new MongoCmdOptionsBuilder()
                .useNoPrealloc(false)
                .useSmallFiles(false)
                .master(false)
                .verbose(false)
                .useNoJournal(false)
                .syncDelay(0)
                .build();

        return new MongodConfigBuilder().version(Version.Main.V4_0)
                .net(new Net("localhost", this.serverPort, Network.localhostIsIPv6()))
                .replication(new Storage(null, "rs0", 5000))
                .configServer(false)
                .cmdOptions(cmdOptions)
                .build();
    }

    @Bean
    public MongoClient mongo(ObjectProvider<MongoClientOptions> options, Environment environment) {
        final MongoProperties mongoProperties = new MongoProperties();
        mongoProperties.setPort(serverPort);
        mongoProperties.setHost("localhost");

        return new MongoClientFactory(mongoProperties, environment).createMongoClient(options.getIfAvailable());
    }
}
