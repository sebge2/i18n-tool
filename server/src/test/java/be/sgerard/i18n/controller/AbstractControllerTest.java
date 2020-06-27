package be.sgerard.i18n.controller;

import be.sgerard.i18n.AbstractIntegrationTest;
import be.sgerard.test.i18n.helper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Sebastien Gerard
 */
public abstract class AbstractControllerTest extends AbstractIntegrationTest {

    @Autowired
    protected TranslationLocaleTestHelper locale;

    @Autowired
    protected UserTestHelper user;

    @Autowired
    protected RepositoryTestHelper repository;

    @Autowired
    protected WorkspaceTestHelper workspace;

    @Autowired
    protected GitRepositoryMockTestHelper gitRepo;

    @Autowired
    protected GitHubRepositoryMockTestHelper gitHub;

    @Autowired
    protected TranslationsTestHelper translations;

    @Autowired
    protected WebTestClient webClient;

}
