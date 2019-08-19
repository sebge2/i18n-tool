package be.sgerard.poc.githuboauth.service.i18n;

import be.sgerard.poc.githuboauth.model.git.GitHubPullRequestEventDto;
import be.sgerard.poc.githuboauth.model.git.PullRequestStatus;
import be.sgerard.poc.githuboauth.model.i18n.WorkspaceStatus;
import be.sgerard.poc.githuboauth.model.i18n.dto.WorkspaceDto;
import be.sgerard.poc.githuboauth.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.event.EventService;
import be.sgerard.poc.githuboauth.service.git.*;
import be.sgerard.poc.githuboauth.service.i18n.persistence.WorkspaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

import static be.sgerard.poc.githuboauth.model.event.Events.EVENT_DELETED_WORKSPACE;
import static be.sgerard.poc.githuboauth.model.event.Events.EVENT_UPDATED_WORKSPACE;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@Service
public class WorkspaceManagerImpl implements WorkspaceManager, WebHookCallback {

    public static final Pattern BRANCHES_TO_KEEP = Pattern.compile("^master|release\\/[0-9]{4}.[0-9]{1,2}$");

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceManagerImpl.class);

    private final WorkspaceRepository repository;
    private final RepositoryManager repositoryManager;
    private final TranslationManager translationManager;
    private final PullRequestManager pullRequestManager;
    private final EventService eventService;

    public WorkspaceManagerImpl(WorkspaceRepository repository,
                                RepositoryManager repositoryManager,
                                TranslationManager translationManager,
                                PullRequestManager pullRequestManager,
                                EventService eventService) {
        this.repositoryManager = repositoryManager;
        this.translationManager = translationManager;
        this.repository = repository;
        this.pullRequestManager = pullRequestManager;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    public List<WorkspaceEntity> findWorkspaces() throws RepositoryException, LockTimeoutException {
        return repositoryManager.open(api -> {
            api.updateLocalRepository();

            final List<String> availableBranches = listBranches(api);

            for (WorkspaceEntity workspaceEntity : repository.findAll()) {
                switch (workspaceEntity.getStatus()) {
                    case IN_REVIEW:
                        updateReviewingWorkspace(
                            workspaceEntity,
                            workspaceEntity.getPullRequestNumber().map(pullRequestManager::getStatus).orElse(null)
                        );

                        break;
                    case NOT_INITIALIZED:
                        if (!availableBranches.contains(workspaceEntity.getBranch())) {
                            deleteWorkspace(workspaceEntity.getId());
                        }

                        break;
                }

                availableBranches.remove(workspaceEntity.getBranch());
            }

            final List<WorkspaceEntity> foundWorkspaces = new ArrayList<>();
            for (String availableBranch : availableBranches) {
                foundWorkspaces.add(createWorkspace(availableBranch));
            }

            repository.saveAll(foundWorkspaces);

            return foundWorkspaces;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceEntity> getWorkspaces() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkspaceEntity> getWorkspace(String id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public WorkspaceEntity initialize(String workspaceId) throws LockTimeoutException, RepositoryException {
        final WorkspaceEntity checkEntity = repository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException(workspaceId));

        if (checkEntity.getStatus() == WorkspaceStatus.INITIALIZED) {
            return checkEntity;
        }

        return repositoryManager.open(api -> {
            try {
                final WorkspaceEntity workspaceEntity = repository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException(workspaceId));

                if (checkEntity.getStatus() == WorkspaceStatus.INITIALIZED) {
                    return checkEntity;
                } else if (workspaceEntity.getStatus() != WorkspaceStatus.NOT_INITIALIZED) {
                    throw new IllegalStateException("The workspace status must be available, but was " + workspaceEntity.getStatus() + ".");
                }

                logger.info("Initialing workspace {}.", workspaceId);

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
        final WorkspaceEntity checkEntity = repository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException(workspaceId));

        if (checkEntity.getStatus() == WorkspaceStatus.IN_REVIEW) {
            return checkEntity;
        }

        return repositoryManager.open(api -> {
            try {
                final WorkspaceEntity workspaceEntity = repository.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException(workspaceId));

                if (workspaceEntity.getStatus() == WorkspaceStatus.IN_REVIEW) {
                    return workspaceEntity;
                } else if (workspaceEntity.getStatus() != WorkspaceStatus.INITIALIZED) {
                    throw new IllegalStateException("The workspace status must be available, but was " + workspaceEntity.getStatus() + ".");
                }

                logger.info("Start reviewing workspace {}.", workspaceId);

                final String pullRequestBranch = workspaceEntity.getPullRequestBranch().orElseThrow(() -> new IllegalStateException("There is no pull request branch."));

                api.checkout(pullRequestBranch);

                translationManager.writeTranslations(workspaceEntity, api);

                api.commitAll(message);

                final int requestNumber = api.getPullRequestManager().createRequest(message, pullRequestBranch, workspaceEntity.getBranch());

                workspaceEntity.setPullRequestNumber(requestNumber);
                workspaceEntity.setStatus(WorkspaceStatus.IN_REVIEW);

                eventService.broadcastEvent(EVENT_UPDATED_WORKSPACE, WorkspaceDto.builder(workspaceEntity).build());

                return workspaceEntity;
            } catch (IOException e) {
                throw new RepositoryException("Error while writing translation files.", e);
            }
        });
    }

    @Override
    @Transactional
    public void updateTranslations(String workspaceId, Map<String, String> translations) throws ResourceNotFoundException {
        final WorkspaceEntity workspace = repository.findById(workspaceId)
            .orElseThrow(() -> new ResourceNotFoundException(workspaceId));

        if (workspace.getStatus() != WorkspaceStatus.INITIALIZED) {
            throw new IllegalStateException("Cannot update translations of workspace [" + workspaceId + "], the status "
                + workspace.getStatus() + " does not allow it.");
        }

        translationManager.updateTranslations(workspace, translations);
    }

    @Override
    @Transactional
    public void deleteWorkspace(String workspaceId) throws RepositoryException, LockTimeoutException {
        final WorkspaceEntity workspaceEntity = repository.findById(workspaceId).orElse(null);

        if (workspaceEntity != null) {
            final String pullRequestBranch = workspaceEntity.getPullRequestBranch().orElse(null);
            if (pullRequestBranch != null) {
                repositoryManager.open(api -> {
                    logger.info("The branch {} has been removed.", pullRequestBranch);

                    api.removeBranch(pullRequestBranch);
                });
            }

            eventService.broadcastEvent(EVENT_DELETED_WORKSPACE, WorkspaceDto.builder(workspaceEntity).build());

            logger.info("The workspace {} has been deleted.", workspaceId);

            repository.delete(workspaceEntity);

            repository.save(createWorkspace(workspaceEntity.getBranch()));
        }
    }

    @Override
    @Transactional
    public void onPullRequest(GitHubPullRequestEventDto event) throws LockTimeoutException, RepositoryException {
        final WorkspaceEntity workspaceEntity = repository
            .findByPullRequestNumber(event.getNumber())
            .orElse(null);

        if (workspaceEntity != null) {
            updateReviewingWorkspace(workspaceEntity, event.getStatus());
        } else {
            logger.info("There is no workspace associated to the pull request {}, not workspace will be updated.", event.getNumber());
        }
    }

    private void updateReviewingWorkspace(WorkspaceEntity workspaceEntity, PullRequestStatus status) throws LockTimeoutException, RepositoryException {
        if (workspaceEntity.getStatus() != WorkspaceStatus.IN_REVIEW) {
            logger.info("The workspace {} is not in review. It won't be updated", workspaceEntity.getId());
        } else if (status == null) {
            logger.error("There is no pull request number while the workspace [" + workspaceEntity.getId() + "] is in review. The workspace won't be updated");
        } else if (!status.isFinished()) {
            logger.info("The pull request {} is not finished, but is {}, nothing will be performed.", workspaceEntity.getPullRequestNumber().orElse(null), status);
        } else {
            logger.info("The pull request is now finished, deleting the workspace {}.", workspaceEntity.getId());

            deleteWorkspace(workspaceEntity.getId());
        }
    }

    private List<String> listBranches(RepositoryAPI api) throws RepositoryException {
        return api.listRemoteBranches()
            .stream()
            .filter(name -> BRANCHES_TO_KEEP.matcher(name).matches())
            .sorted((first, second) -> {
                if (Objects.equals(first, second)) {
                    return 0;
                } else if (RepositoryManagerImpl.DEFAULT_BRANCH.equals(first)) {
                    return -1;
                } else if (RepositoryManagerImpl.DEFAULT_BRANCH.equals(second)) {
                    return 1;
                } else {
                    return Comparator.<String>reverseOrder().compare(first, second);
                }
            })
            .collect(toList());
    }

    private WorkspaceEntity createWorkspace(String availableBranch) {
        final WorkspaceEntity workspaceEntity = new WorkspaceEntity(availableBranch);

        eventService.broadcastEvent(EVENT_UPDATED_WORKSPACE, WorkspaceDto.builder(workspaceEntity).build());

        return workspaceEntity;
    }
}
