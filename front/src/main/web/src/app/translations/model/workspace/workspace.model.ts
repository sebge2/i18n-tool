import {WorkspaceStatus} from './workspace-status.model';
import {WorkspaceDto} from "../../../api";

export class Workspace {

    public static fromDto(dto: WorkspaceDto): Workspace {
        return new Workspace(
            dto.id,
            dto.branch,
            WorkspaceStatus[dto.status],
            dto.repositoryId
        );
    }

    constructor(public id: string,
                public branch: string,
                public status: WorkspaceStatus,
                public repositoryId: string) {
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

    public equals(other: Workspace): boolean {
        return this.id === other.id;
    }
}
