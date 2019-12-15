import {WorkspaceReview, WorkspaceReviewType} from "./workspace-review.model";

export class WorkspaceGithubReview implements WorkspaceReview {

    constructor(public pullRequestBranch: string,
                public pullRequestNumber: number) {
    }

    getType(): WorkspaceReviewType {
        return WorkspaceReviewType.GITHUB;
    }

}
