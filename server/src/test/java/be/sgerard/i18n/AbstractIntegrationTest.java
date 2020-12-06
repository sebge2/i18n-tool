package be.sgerard.i18n;

import be.sgerard.test.i18n.DatabaseTestHelper;
import be.sgerard.test.i18n.helper.*;
import be.sgerard.test.i18n.helper.repository.RemoteRepositoryTestHelper;
import be.sgerard.test.i18n.support.DatabaseCleanupTestExecutionListener;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = {I18nToolApplication.class}, properties = "spring.main.web-application-type=reactive")
@ComponentScan({"be.sgerard.i18n", "be.sgerard.test.i18n"})
@AutoConfigureDataMongo
@AutoConfigureWebTestClient(timeout = "30000")
@TestExecutionListeners(
        value = {DatabaseCleanupTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@ActiveProfiles("test")
@Tag("IntegrationTest")
public abstract class AbstractIntegrationTest {

    @Autowired
    protected TranslationLocaleTestHelper locale;

    @Autowired
    protected UserTestHelper user;

    @Autowired
    protected RepositoryTestHelper repository;

    @Autowired
    protected RemoteRepositoryTestHelper remoteRepository;

    @Autowired
    protected WorkspaceTestHelper workspace;

    @Autowired
    protected TranslationsTestHelper translations;

    @Autowired
    protected SnapshotTestHelper snapshot;

    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected DatabaseTestHelper database;

}
