package be.sgerard.i18n.service.git;

import be.sgerard.i18n.model.github.GitHubPullRequestEventDto;
import be.sgerard.i18n.service.github.GitHubWebHookCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Sebastien Gerard
 */
@Component
public class PullRequestGithubWebHookEventHandler implements GithubWebHookEventHandler {

    public static final String PULL_REQUEST_EVENT = "pull_request";

    private final ObjectMapper objectMapper;

    public PullRequestGithubWebHookEventHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean support(String eventType) {
        return Objects.equals(PULL_REQUEST_EVENT, eventType);
    }

    @Override
    public void call(String eventType, String payload, GitHubWebHookCallback callback) throws Exception {
        callback.onPullRequestUpdate(deserialize(payload));

//        if (workspaceEntity.getStatus() != WorkspaceStatus.IN_REVIEW) {
//            logger.info("The workspace {} is not in review. It won't be updated", workspaceEntity.getId());
//        } else if (status == null) {
//            logger.error("There is no pull request number while the workspace [" + workspaceEntity.getId() + "] is in review. The workspace won't be updated");
//        } else if (!status.isFinished()) {
//            logger.info("The pull request {} is not finished, but is {}, nothing will be performed.", workspaceEntity.getPullRequestNumber().orElse(null), status);
//        } else {
//            logger.info("The pull request is now finished, deleting the workspace {}.", workspaceEntity.getId());
//
//            delete(workspaceEntity.getId());
//
//            createWorkspace(workspaceEntity.getBranch());
//        }

    }


    //    @Override
//    @Transactional
//    public void onPullRequestUpdate(GitHubPullRequestEventDto event) throws LockTimeoutException, RepositoryException {
//        final WorkspaceEntity workspaceEntity = repository
//                .findByPullRequestNumber(event.getNumber())
//                .orElse(null);
//
//        if (workspaceEntity != null) {
//            updateReviewingWorkspace(workspaceEntity, event.getStatus());
//        } else {
//            logger.info("There is no workspace associated to the pull request {}, not workspace will be updated.", event.getNumber());
//        }
//    }
//
//    @Override
//    @Transactional
//    public void onCreatedBranch(String branch) {
//        if (canBeAssociatedToWorkspace(branch)) {
//            createWorkspace(branch);
//        }
//    }
//
//    @Override
//    @Transactional
//    public void onDeletedBranch(String branch) {
//        final Optional<WorkspaceEntity> entity = repository.findByBranch(branch);
//
//        if (entity.isPresent()) {
//            delete(entity.get().getId());
//        } else {
//            logger.debug("There is no workspace associated to the branch {}.", branch);
//        }
//    }

    private GitHubPullRequestEventDto deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, GitHubPullRequestEventDto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to parse response.", e);
        }
    }
}
