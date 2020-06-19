package be.sgerard.i18n.controller;

import be.sgerard.i18n.AbstractIntegrationTest;
import be.sgerard.test.i18n.helper.*;
import be.sgerard.test.i18n.helper.AsyncMockMvcTestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Sebastien Gerard
 */
public abstract class AbstractControllerTest extends AbstractIntegrationTest {

    @Autowired
    protected ObjectMapper objectMapper;

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
    protected MockMvc mvc;

    @Autowired
    protected AsyncMockMvcTestHelper asyncMvc;

}
