package be.sgerard.i18n.controller;

import be.sgerard.i18n.AbstractIntegrationTest;
import be.sgerard.test.i18n.RepositoryTestHelper;
import be.sgerard.test.i18n.TranslationLocaleTestHelper;
import be.sgerard.test.i18n.UserTestHelper;
import be.sgerard.test.i18n.WorkspaceTestHelper;
import be.sgerard.test.i18n.support.AsyncMockMvcTestHelper;
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
    protected TranslationLocaleTestHelper localeTestHelper;

    @Autowired
    protected UserTestHelper userTestHelper;

    @Autowired
    protected RepositoryTestHelper repositoryTestHelper;

    @Autowired
    protected WorkspaceTestHelper workspaceTestHelper;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AsyncMockMvcTestHelper asyncMockMvc;

}
