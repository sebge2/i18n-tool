import {Component, Input} from '@angular/core';
import {WorkspaceTreeNode} from "../repository-details-workspaces.component";
import {WorkspaceStatus} from "../../../../../../translations/model/workspace/workspace-status.model";
import {WorkspaceService} from "../../../../../../translations/service/workspace.service";
import {NotificationService} from "../../../../../../core/notification/service/notification.service";
import {
    GitHubFileLink,
    GitHubPRLink
} from "../../../../../../core/shared/component/button/git-hub-link-button/git-hub-link-button.component";
import {RepositoryType} from "../../../../../../translations/model/repository/repository-type.model";
import {GitHubRepository} from "../../../../../../translations/model/repository/github-repository.model";
import {WorkspaceGithubReview} from "../../../../../../translations/model/workspace/workspace-github-review.model";

@Component({
    selector: 'app-repository-details-workspace-node',
    templateUrl: './repository-details-workspace-node.component.html',
    styleUrls: ['./repository-details-workspace-node.component.css']
})
export class RepositoryDetailsWorkspaceNodeComponent {

    @Input() public node: WorkspaceTreeNode;
    @Input() public loading: boolean = false;

    public initializationInProgress: boolean = false;
    public deleteInProgress: boolean = false;
    public syncInProgress: boolean = false;

    constructor(private _workspaceService: WorkspaceService,
                private _notificationService: NotificationService) {
    }

    public get name(): string {
        return this.node.workspace.branch;
    }

    public get badgeClass(): string {
        switch (this.node.workspace.status) {
            case WorkspaceStatus.IN_REVIEW:
            case WorkspaceStatus.INITIALIZED:
                return 'app-badge-success';
            case WorkspaceStatus.NOT_INITIALIZED:
                return 'app-badge-warning';
            default:
                return '';
        }
    }

    public get badgeText(): string {
        switch (this.node.workspace.status) {
            case WorkspaceStatus.IN_REVIEW:
                return 'ADMIN.REPOSITORIES.DETAILS_CARD.WORKSPACES.IN_REVIEW';
            case WorkspaceStatus.INITIALIZED:
                return 'ADMIN.REPOSITORIES.DETAILS_CARD.WORKSPACES.READY';
            case WorkspaceStatus.NOT_INITIALIZED:
                return 'ADMIN.REPOSITORIES.DETAILS_CARD.WORKSPACES.NOT_READY';
            default:
                return '';
        }
    }

    public get initializationAllowed() {
        return this.node.workspace.status === WorkspaceStatus.NOT_INITIALIZED;
    }

    public get syncAllowed(): boolean {
        return (this.node.workspace.status === WorkspaceStatus.INITIALIZED) || (this.node.workspace.status === WorkspaceStatus.IN_REVIEW);
    }

    public onInitialize() {
        this.initializationInProgress = true;

        this._workspaceService
            .initialize(this.node.workspace.id)
            .toPromise()
            .catch(error => {
                console.error('Error while initializing workspace.', error);
                this._notificationService.displayErrorMessage('ADMIN.WORKSPACES.ERROR.INITIALIZE_WORKSPACE', error);
            })
            .finally(() => this.initializationInProgress = false);
    }

    public onDelete() {
        this.deleteInProgress = true;

        this._workspaceService
            .delete(this.node.workspace)
            .toPromise()
            .catch(error => {
                console.error('Error while deleting workspace.', error);
                this._notificationService.displayErrorMessage('ADMIN.WORKSPACES.ERROR.DELETE_WORKSPACE', error);
            })
            .finally(() => this.deleteInProgress = false);
    }

    public onSynchronize() {
        this.syncInProgress = true;

        this._workspaceService
            .synchronizeWorkspace(this.node.workspace)
            .toPromise()
            .catch(error => {
                console.error('Error while synchronizing workspace.', error);
                this._notificationService.displayErrorMessage('ADMIN.WORKSPACES.ERROR.WORKSPACE_SYNCHRONIZE', error);
            })
            .finally(() => this.syncInProgress = false);
    }

    public get workspaceGitHubLink(): GitHubFileLink {
        if (this.node
            && this.node.repository.type === RepositoryType.GITHUB
            && this.node.workspace.status !== WorkspaceStatus.IN_REVIEW) {
            const repository = <GitHubRepository>this.node.repository;

            return new GitHubFileLink(
                repository.username,
                repository.repository,
                this.node.workspace.branch
            );
        } else {
            return null;
        }
    }

    public get prGitHubLink(): GitHubPRLink {
        if (this.node
            && this.node.repository.type === RepositoryType.GITHUB
            && this.node.workspace.status === WorkspaceStatus.IN_REVIEW) {
            const repository = <GitHubRepository>this.node.repository;

            return new GitHubPRLink(
                repository.username,
                repository.repository,
                (<WorkspaceGithubReview>this.node.workspace.review).pullRequestNumber
            );
        } else {
            return null;
        }
    }
}
