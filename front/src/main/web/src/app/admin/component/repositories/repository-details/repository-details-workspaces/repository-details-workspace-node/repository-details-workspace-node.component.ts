import { Component, Input } from '@angular/core';
import { WorkspaceTreeNode } from '../repository-details-workspaces.component';
import { WorkspaceStatus } from '@i18n-core-translation';
import { WorkspaceService } from '@i18n-core-translation';
import { NotificationService } from '@i18n-core-notification';
import { GitHubFileLink, GitHubPRLink } from '@i18n-core-shared';
import { RepositoryType } from '@i18n-core-translation';
import { GitHubRepository } from '@i18n-core-translation';
import { WorkspaceGithubReview } from '@i18n-core-translation';

@Component({
  selector: 'app-repository-details-workspace-node',
  templateUrl: './repository-details-workspace-node.component.html',
  styleUrls: ['./repository-details-workspace-node.component.css'],
})
export class RepositoryDetailsWorkspaceNodeComponent {
  @Input() node: WorkspaceTreeNode;
  @Input() loading: boolean = false;

  initializationInProgress: boolean = false;
  deleteInProgress: boolean = false;
  syncInProgress: boolean = false;

  constructor(private _workspaceService: WorkspaceService, private _notificationService: NotificationService) {}

  get name(): string {
    return this.node.workspace.branch;
  }

  get badgeClass(): string {
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

  get badgeText(): string {
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

  get initializationAllowed() {
    return this.node.workspace.status === WorkspaceStatus.NOT_INITIALIZED;
  }

  get syncAllowed(): boolean {
    return (
      this.node.workspace.status === WorkspaceStatus.INITIALIZED ||
      this.node.workspace.status === WorkspaceStatus.IN_REVIEW
    );
  }

  onInitialize() {
    this.initializationInProgress = true;

    this._workspaceService
      .initialize(this.node.workspace.id)
      .toPromise()
      .catch((error) => {
        console.error('Error while initializing workspace.', error);
        this._notificationService.displayErrorMessage('ADMIN.WORKSPACES.ERROR.INITIALIZE_WORKSPACE', error);
      })
      .finally(() => (this.initializationInProgress = false));
  }

  onDelete() {
    this.deleteInProgress = true;

    this._workspaceService
      .delete(this.node.workspace)
      .toPromise()
      .catch((error) => {
        console.error('Error while deleting workspace.', error);
        this._notificationService.displayErrorMessage('ADMIN.WORKSPACES.ERROR.DELETE_WORKSPACE', error);
      })
      .finally(() => (this.deleteInProgress = false));
  }

  onSynchronize() {
    this.syncInProgress = true;

    this._workspaceService
      .synchronizeWorkspace(this.node.workspace)
      .toPromise()
      .catch((error) => {
        console.error('Error while synchronizing workspace.', error);
        this._notificationService.displayErrorMessage('ADMIN.WORKSPACES.ERROR.WORKSPACE_SYNCHRONIZE', error);
      })
      .finally(() => (this.syncInProgress = false));
  }

  get workspaceGitHubLink(): GitHubFileLink {
    if (
      this.node &&
      this.node.repository.type === RepositoryType.GITHUB &&
      this.node.workspace.status !== WorkspaceStatus.IN_REVIEW
    ) {
      const repository = <GitHubRepository>this.node.repository;

      return new GitHubFileLink(repository.username, repository.repository, this.node.workspace.branch);
    } else {
      return null;
    }
  }

  get prGitHubLink(): GitHubPRLink {
    if (
      this.node &&
      this.node.repository.type === RepositoryType.GITHUB &&
      this.node.workspace.status === WorkspaceStatus.IN_REVIEW
    ) {
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
