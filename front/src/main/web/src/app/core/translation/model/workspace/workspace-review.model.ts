export abstract class WorkspaceReview {
  abstract getType(): WorkspaceReviewType;
}

export enum WorkspaceReviewType {
  GITHUB = 'GITHUB',
}
