import {WorkspaceStatus} from './workspace-status.model';
import {WorkspaceDto} from "../../../api";
import {RepositoryStatus} from "../repository/repository-status.model";
import {RepositoryType} from "../repository/repository-type.model";

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
            RepositoryType[dto.repositoryType]
        );
    }

    constructor(public id: string,
                public branch: string,
                public status: WorkspaceStatus,
                public defaultWorkspace: boolean,
                public repositoryId: string,
                public repositoryName: string,
                public repositoryStatus: RepositoryStatus,
                public repositoryType: RepositoryType) {
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
}
