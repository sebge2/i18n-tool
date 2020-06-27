package be.sgerard.i18n;

import be.sgerard.test.i18n.support.TransactionInvocationInterceptor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {I18nToolApplication.class, TestConfiguration.class}, properties = "spring.main.web-application-type=reactive")
@ComponentScan({"be.sgerard.i18n", "be.sgerard.test.i18n"})
@AutoConfigureDataMongo
@AutoConfigureWebTestClient
@ExtendWith(TransactionInvocationInterceptor.class)
@ActiveProfiles("test")
@Tag("IntegrationTest")
public abstract class AbstractIntegrationTest {

}
