package be.sgerard.poc.githuboauth.service.i18n;

import be.sgerard.poc.githuboauth.model.i18n.TranslationBundleFileEntity;
import be.sgerard.poc.githuboauth.model.i18n.TranslationWorkspaceEntity;
import be.sgerard.poc.githuboauth.model.i18n.TranslationWorkspaceStatus;
import be.sgerard.poc.githuboauth.model.i18n.file.TranslationBundleFileDto;
import be.sgerard.poc.githuboauth.model.i18n.file.TranslationFileEntryDto;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.git.RepositoryException;
import be.sgerard.poc.githuboauth.service.git.RepositoryManager;
import be.sgerard.poc.githuboauth.service.i18n.file.TranslationBundleWalker;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
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
    public void loadTranslations(String workspaceId) throws LockTimeoutException, RepositoryException, TranslationLoadingException {
        final TranslationWorkspaceEntity workspaceEntity = repository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException(workspaceId));

        if (workspaceEntity.getStatus() != TranslationWorkspaceStatus.AVAILABLE) {
            throw new IllegalStateException("The workspace status must be available, but was " + workspaceEntity.getStatus() + ".");
        }

        repositoryManager.browseBranch(workspaceEntity.getBranch(), api -> {
            try {
                walker.walk(api, (bundleFile, entries) -> onBundleFound(workspaceEntity, bundleFile, entries));
            } catch (IOException e) {
                throw new TranslationLoadingException("Error while loading translation bundle files.", e);
            }
        });

        workspaceEntity.setStatus(TranslationWorkspaceStatus.AVAILABLE);
        workspaceEntity.setLoadingTime(Instant.now());
    }

    private void onBundleFound(TranslationWorkspaceEntity workspaceEntity,
                               TranslationBundleFileDto bundleFile,
                               Stream<TranslationFileEntryDto> entries) {
        workspaceEntity.getFiles().add(
                new TranslationBundleFileEntity(workspaceEntity, bundleFile.getName(), bundleFile.getLocationDirectory().toString())
        );

        // TODO
    }
}
