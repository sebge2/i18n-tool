package be.sgerard.test.i18n.mock.repository.git;

import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApiProvider;
import be.sgerard.test.i18n.helper.repository.RemoteRepositoryTestHelper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author Sebastien Gerard
 */
@Component
@Primary
public class GitRepositoryApiMockProvider implements GitRepositoryApiProvider {

    private final RemoteRepositoryTestHelper remoteRepositoryTestHelper;

    public GitRepositoryApiMockProvider(RemoteRepositoryTestHelper remoteRepositoryTestHelper) {
        this.remoteRepositoryTestHelper = remoteRepositoryTestHelper;
    }

    @Override
    public GitRepositoryApi initApi(GitRepositoryApi.Configuration configuration) {
        return remoteRepositoryTestHelper.openApi(configuration);
    }
}
