package be.sgerard.poc.githuboauth.service.i18n;

import be.sgerard.poc.githuboauth.model.git.CommitRequest;
import be.sgerard.poc.githuboauth.model.i18n.WorkspaceStatus;
import be.sgerard.poc.githuboauth.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.poc.githuboauth.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleFileEntity;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleKeyEntryEntity;
import be.sgerard.poc.githuboauth.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.auth.AuthenticationManager;
import be.sgerard.poc.githuboauth.service.git.RepositoryException;
import be.sgerard.poc.githuboauth.service.git.RepositoryManager;
import be.sgerard.poc.githuboauth.service.i18n.file.TranslationBundleWalker;
import be.sgerard.poc.githuboauth.service.i18n.persistence.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Sebastien Gerard
 */
@Service
public class WorkspaceManagerImpl implements WorkspaceManager {

    private final RepositoryManager repositoryManager;
    private final AuthenticationManager authenticationManager;
    private final TranslationBundleWalker walker;
    private final WorkspaceRepository workspaceRepository;

    public WorkspaceManagerImpl(RepositoryManager repositoryManager,
                                AuthenticationManager authenticationManager,
                                TranslationBundleWalker walker,
                                WorkspaceRepository workspaceRepository) {
        this.repositoryManager = repositoryManager;
        this.authenticationManager = authenticationManager;
        this.walker = walker;
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    @Transactional
    public List<WorkspaceEntity> findWorkspaces() throws RepositoryException, LockTimeoutException {
        return repositoryManager.open(api -> {
            final List<String> availableBranches = api.listBranches();

            workspaceRepository.findAll()
                    .forEach(workspaceEntity -> {
                        if (!availableBranches.contains(workspaceEntity.getBranch())
                                && (workspaceEntity.getStatus() == WorkspaceStatus.NOT_INITIALIZED)) {
                            workspaceRepository.delete(workspaceEntity);
                        }

                        availableBranches.remove(workspaceEntity.getBranch());
                    });

            final List<WorkspaceEntity> foundWorkspaces = new ArrayList<>();
            for (String availableBranch : availableBranches) {
                foundWorkspaces.add(new WorkspaceEntity(availableBranch));
            }

            workspaceRepository.saveAll(foundWorkspaces);

            return foundWorkspaces;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceEntity> getWorkspaces() {
        return workspaceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkspaceEntity> getWorkspace(String id) {
        return workspaceRepository.findById(id);
    }

    @Override
    @Transactional
    public WorkspaceEntity initialize(String workspaceId) throws LockTimeoutException, RepositoryException {
        return repositoryManager.open(api -> {
            try {
                final WorkspaceEntity workspaceEntity = workspaceRepository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException(workspaceId));

                if (workspaceEntity.getStatus() != WorkspaceStatus.NOT_INITIALIZED) {
                    throw new IllegalStateException("The workspace status must be available, but was " + workspaceEntity.getStatus() + ".");
                }

                final Instant now = Instant.now();
                final String pullRequestBranch = workspaceEntity.getBranch() + "_i18n_" + LocalDate.ofInstant(now, ZoneId.systemDefault()).toString();

                workspaceEntity.setStatus(WorkspaceStatus.INITIALIZED);
                workspaceEntity.setInitializationTime(now);
                workspaceEntity.setPullRequestBranch(pullRequestBranch);

                api.checkout(workspaceEntity.getBranch());

                api.updateLocalRepository();

                api.createBranch(pullRequestBranch);

                walker.walk(api, (bundleFile, entries) -> onBundleFound(workspaceEntity, bundleFile, entries));

                return workspaceEntity;
            } catch (IOException e) {
                throw new RepositoryException("Error while loading translation bundle files.", e);
            }
        });
    }

    @Override
    @Transactional
    public WorkspaceEntity startReviewing(String id, String message) throws ResourceNotFoundException, LockTimeoutException, RepositoryException {
        final WorkspaceEntity workspace = getWorkspace(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        if (workspace.getStatus() != WorkspaceStatus.INITIALIZED) {
            throw new IllegalStateException("Cannot update translations of workspace [" + id + "], the status "
                    + workspace.getStatus() + " does not allow it.");
        }

        repositoryManager.open(api -> {
            final String pullRequestBranch = workspace.getPullRequestBranch().orElseThrow(() -> new IllegalStateException("There is no pull request branch."));

            api.checkout(pullRequestBranch);

//            translationManager.updateBundleFiles(workspace); TODO

//            api.commitAll(new CommitRequest(message, authenticationManager.getCurrentUser().getUsername(), authenticationManager.getCurrentUser().getEmail()));

            api.getPullRequestManager().createRequest(message, pullRequestBranch, workspace.getBranch());
        });

        workspace.setStatus(WorkspaceStatus.IN_REVIEW);

        return workspace;
    }

    @Override
    @Transactional
    public void deleteWorkspace(String workspaceId) {
        workspaceRepository.findById(workspaceId).ifPresent(workspaceRepository::delete);
    }

    private void onBundleFound(WorkspaceEntity workspaceEntity,
                               ScannedBundleFileDto bundleFile,
                               Stream<ScannedBundleFileKeyDto> entries) {
        final BundleFileEntity bundleFileEntity =
                new BundleFileEntity(workspaceEntity, bundleFile.getName(), bundleFile.getLocationDirectory().toString());

        entries.forEach(
                entry -> {
                    final BundleKeyEntity keyEntity = new BundleKeyEntity(bundleFileEntity, entry.getKey());

                    for (Map.Entry<Locale, String> translationEntry : entry.getTranslations().entrySet()) {
                        new BundleKeyEntryEntity(keyEntity, translationEntry.getKey().toLanguageTag(), translationEntry.getValue());
                    }
                });
    }
}
