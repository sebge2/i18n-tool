package be.sgerard.i18n.service.github.external;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * All supported GitHub Web-hook event type.
 *
 * @author Sebastien Gerard
 */
enum GitHubEventType {

    /**
     * Pull-request events.
     */
    PULL_REQUEST("pull_request", GitHubPullRequestEventDto.class),

    /**
     * Creation of branch.
     */
    BRANCH_CREATED("create", GitHubBranchCreatedEventDto.class),

    /**
     * Deletion of branch.
     */
    BRANCH_DELETED("delete", GitHubBranchDeletedEventDto.class);

    private final String type;
    private final Class<? extends BaseGitHubWebHookEventDto> dtoType;

    GitHubEventType(String type, Class<? extends BaseGitHubWebHookEventDto> dtoType) {
        this.type = type;
        this.dtoType = dtoType;
    }

    /**
     * Returns the {@link BaseGitHubWebHookEventDto DTO} associated to this type.
     */
    public Class<? extends BaseGitHubWebHookEventDto> getDtoType() {
        return dtoType;
    }

    /**
     * Returns the matching event type.
     */
    public static Optional<GitHubEventType> findType(String eventType) {
        return Stream.of(values())
                .filter(currentType -> Objects.equals(currentType.type, eventType.trim().toLowerCase()))
                .findFirst();
    }
}
