package be.sgerard.poc.githuboauth.service.i18n;

import be.sgerard.poc.githuboauth.model.i18n.WorkspaceStatus;
import be.sgerard.poc.githuboauth.model.i18n.dto.WorkspaceDto;
import be.sgerard.poc.githuboauth.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.event.EventService;
import be.sgerard.poc.githuboauth.service.git.RepositoryException;
import be.sgerard.poc.githuboauth.service.git.RepositoryManager;
import be.sgerard.poc.githuboauth.service.i18n.persistence.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.sgerard.poc.githuboauth.model.event.Events.EVENT_DELETED_WORKSPACE;
import static be.sgerard.poc.githuboauth.model.event.Events.EVENT_UPDATED_WORKSPACE;

/**
 * @author Sebastien Gerard
 */
@Service
public class WorkspaceManagerImpl implements WorkspaceManager {

    private final WorkspaceRepository workspaceRepository;
    private final RepositoryManager repositoryManager;
    private final TranslationManager translationManager;
    private final EventService eventService;

    public WorkspaceManagerImpl(WorkspaceRepository workspaceRepository,
                                RepositoryManager repositoryManager,
                                TranslationManager translationManager,
                                EventService eventService) {
        this.repositoryManager = repositoryManager;
        this.translationManager = translationManager;
        this.workspaceRepository = workspaceRepository;
        this.eventService = eventService;
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
                            deleteWorkspace(workspaceEntity.getId());
                        }

                        availableBranches.remove(workspaceEntity.getBranch());
                    });

            final List<WorkspaceEntity> foundWorkspaces = new ArrayList<>();
            for (String availableBranch : availableBranches) {
                final WorkspaceEntity workspaceEntity = new WorkspaceEntity(availableBranch);

                foundWorkspaces.add(workspaceEntity);

                eventService.broadcastEvent(EVENT_UPDATED_WORKSPACE, WorkspaceDto.builder(workspaceEntity).build());
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
        final WorkspaceEntity checkEntity = workspaceRepository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException(workspaceId));

        if (checkEntity.getStatus() == WorkspaceStatus.INITIALIZED) {
            return checkEntity;
        }

        return repositoryManager.open(api -> {
            try {
                final WorkspaceEntity workspaceEntity = workspaceRepository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException(workspaceId));

                if (checkEntity.getStatus() == WorkspaceStatus.INITIALIZED) {
                    return checkEntity;
                } else if (workspaceEntity.getStatus() != WorkspaceStatus.NOT_INITIALIZED) {
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

                translationManager.readTranslations(workspaceEntity, api);

                eventService.broadcastEvent(EVENT_UPDATED_WORKSPACE, WorkspaceDto.builder(workspaceEntity).build());

                return workspaceEntity;
            } catch (IOException e) {
                throw new RepositoryException("Error while loading translation files.", e);
            }
        });
    }

    @Override
    @Transactional
    public WorkspaceEntity startReviewing(String workspaceId, String message) throws ResourceNotFoundException, LockTimeoutException, RepositoryException {
        final WorkspaceEntity checkEntity = workspaceRepository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException(workspaceId));

        if (checkEntity.getStatus() == WorkspaceStatus.IN_REVIEW) {
            return checkEntity;
        }

        return repositoryManager.open(api -> {
            try {
                final WorkspaceEntity workspaceEntity = workspaceRepository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException(workspaceId));

                if (workspaceEntity.getStatus() == WorkspaceStatus.IN_REVIEW) {
                    return workspaceEntity;
                } else if (workspaceEntity.getStatus() != WorkspaceStatus.INITIALIZED) {
                    throw new IllegalStateException("The workspace status must be available, but was " + workspaceEntity.getStatus() + ".");
                }

                final String pullRequestBranch = workspaceEntity.getPullRequestBranch().orElseThrow(() -> new IllegalStateException("There is no pull request branch."));

                api.checkout(pullRequestBranch);

                translationManager.writeTranslations(workspaceEntity, api);

                api.commitAll(message);

                api.getPullRequestManager().createRequest(message, pullRequestBranch, workspaceEntity.getBranch());

                workspaceEntity.setStatus(WorkspaceStatus.IN_REVIEW);

                return workspaceEntity;
            } catch (IOException e) {
                throw new RepositoryException("Error while writing translation files.", e);
            }
        });
    }

    @Override
    @Transactional
    public void deleteWorkspace(String workspaceId) {
        workspaceRepository.findById(workspaceId).ifPresent(
                workspace -> {
                    eventService.broadcastEvent(EVENT_DELETED_WORKSPACE, WorkspaceDto.builder(workspace).build());

                    workspaceRepository.delete(workspace);
                }
        );
    }
}
