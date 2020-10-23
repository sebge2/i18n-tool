import {WorkspaceGitHubReviewDto, WorkspaceReviewDto} from "../../../api";
import {WorkspaceGithubReview} from "./workspace-github-review.model";
import {WorkspaceReview} from "./workspace-review.model";

export function fromWorkspaceReviewDto(dto: WorkspaceReviewDto): WorkspaceReview {
    if (!dto) {
        return null;
    } else if (dto.type === "GIT_HUB") {
        const gitHubReview = <WorkspaceGitHubReviewDto>dto;

        return new WorkspaceGithubReview(gitHubReview.pullRequestBranch, gitHubReview.pullRequestNumber);
    } else {
        throw Error(`Unsupported type ${dto.type}.`);
    }
}
