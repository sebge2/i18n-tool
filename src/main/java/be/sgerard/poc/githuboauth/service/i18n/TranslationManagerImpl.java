package be.sgerard.poc.githuboauth.service.i18n;

import be.sgerard.poc.githuboauth.model.i18n.TranslationWorkspaceEntity;
import be.sgerard.poc.githuboauth.model.i18n.TranslationWorkspaceStatus;
import be.sgerard.poc.githuboauth.model.i18n.file.TranslationBundleFileDto;
import be.sgerard.poc.githuboauth.model.i18n.file.TranslationFileEntryDto;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.git.RepositoryException;
import be.sgerard.poc.githuboauth.service.git.RepositoryManager;
import be.sgerard.poc.githuboauth.service.i18n.file.TranslationBundleWalker;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Sebastien Gerard
 */
@Service
public class TranslationManagerImpl implements TranslationManager {

    private final RepositoryManager repositoryManager;
    private final TranslationBundleWalker walker;
    private final TranslationWorkspaceRepository repository;

    public TranslationManagerImpl(RepositoryManager repositoryManager,
                                  TranslationBundleWalker walker,
                                  TranslationWorkspaceRepository repository) {
        this.repositoryManager = repositoryManager;
        this.walker = walker;
        this.repository = repository;
    }

    @Override
    @Transactional
    public void scanBranches() throws RepositoryException, LockTimeoutException {
        final List<String> availableBranches = new ArrayList<>(repositoryManager.listBranches());

        StreamSupport.stream(repository.findAll().spliterator(), false)
                .forEach(workspaceEntity -> {
                    if (!availableBranches.contains(workspaceEntity.getBranch())) {
                        if (workspaceEntity.getStatus() == TranslationWorkspaceStatus.AVAILABLE) {
                            repository.delete(workspaceEntity);
                        } else {
                            // TODO what to do?
                        }
                    }

                    availableBranches.remove(workspaceEntity.getBranch());
                });

        for (String availableBranch : availableBranches) {
            repository.save(new TranslationWorkspaceEntity(availableBranch));
        }
    }

    @Override
    public List<TranslationWorkspaceEntity> getWorkspaces() {
        return repository.findAll();
    }

    @Override
    public Optional<TranslationWorkspaceEntity> getWorkspace(String id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public void loadTranslations(String branchName) throws LockTimeoutException, RepositoryException, TranslationLoadingException {
        // TODO not already loaded
        repositoryManager.browseBranch(branchName, api -> {
            try {
                walker.walk(api, this::onBundleFound);
            } catch (IOException e) {
                throw new TranslationLoadingException("Error while loading translation bundle files.", e);
            }
        });
    }

    private void onBundleFound(TranslationBundleFileDto bundleFile, Stream<TranslationFileEntryDto> entries) {
        System.out.println(bundleFile);
    }
}
