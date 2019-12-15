import {WorkspaceStatus} from './workspace-status.model';
import {WorkspaceDto} from "../../../api";
import {RepositoryStatus} from "../repository/repository-status.model";
import {RepositoryType} from "../repository/repository-type.model";
import {WorkspaceReview} from "./workspace-review.model";

export class Workspace {

    public static fromDto(dto: WorkspaceDto): Workspace {
        return new Workspace(
            dto.id,
            dto.branch,
            WorkspaceStatus[dto.status],
            dto.defaultWorkspace,
            dto.repositoryId,
            dto.repositoryName,
            RepositoryStatus[dto.repositoryStatus],
            RepositoryType[dto.repositoryType],
            dto.numberBundleKeys,
            dto.dirty,
            WorkspaceReview.fromDto(dto.review)
        );
    }

    constructor(public id: string,
                public branch: string,
                public status: WorkspaceStatus,
                public defaultWorkspace: boolean,
                public repositoryId: string,
                public repositoryName: string,
                public repositoryStatus: RepositoryStatus,
                public repositoryType: RepositoryType,
                public numberBundleKeys: number,
                public dirty: boolean,
                public review: WorkspaceReview) {
    }

    public isNotInitialized(): boolean {
        return this.status == WorkspaceStatus.NOT_INITIALIZED;
    }

    public isInitialized(): boolean {
        return this.status == WorkspaceStatus.INITIALIZED;
    }

    public isInReview(): boolean {
        return this.status == WorkspaceStatus.IN_REVIEW;
    }

    public isDirty(): boolean {
        return this.dirty;
    }
}
