import {WorkspaceGitHubReviewDto, WorkspaceReviewDto} from "../../../api";
import {WorkspaceGithubReview} from "./workspace-github-review.model";


export abstract class WorkspaceReview {

    static fromDto(dto: WorkspaceReviewDto): WorkspaceReview {
        if (!dto) {
            return null;
        } else if (dto.type === "GIT_HUB") {
            const gitHubReview = <WorkspaceGitHubReviewDto>dto;

            return new WorkspaceGithubReview(gitHubReview.pullRequestBranch, gitHubReview.pullRequestNumber);
        } else {
            throw Error(`Unsupported type ${dto.type}.`);
        }
    }

    abstract getType(): WorkspaceReviewType;

}

export enum WorkspaceReviewType {

    GITHUB = "GITHUB"
}
