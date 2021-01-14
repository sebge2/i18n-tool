import {Component, Input, OnInit} from '@angular/core';
import {Repository} from "../../../../../translations/model/repository/repository.model";
import {RepositoryStatus} from "../../../../../translations/model/repository/repository-status.model";
import {RepositoryService} from "../../../../../translations/service/repository.service";
import {NotificationService} from "../../../../../core/notification/service/notification.service";
import {
    EmptyTreeObjectDataSource,
    TreeObject,
    TreeObjectDataSource
} from "../../../../../core/shared/component/tree/tree.component";
import {Observable, of} from "rxjs";
import {Workspace} from "../../../../../translations/model/workspace/workspace.model";
import {WorkspaceService} from "../../../../../translations/service/workspace.service";
import {map} from "rxjs/operators";
import {BundleFile} from "../../../../../translations/model/workspace/bundle-file.model";
import {BundleFileEntry} from "../../../../../translations/model/workspace/bundle-file-entry.model";
import {RepositoryDetailsWorkspaceTreeNodeComponent} from './repository-details-workspace-tree-node/repository-details-workspace-tree-node.component';
import * as _ from "lodash";
import {MatDialog} from "@angular/material/dialog";
import {RepositoryDetailsTranslationsConfigurationComponent} from "./repository-details-translations-configuration/repository-details-translations-configuration.component";

export class WorkspaceTreeNode implements TreeObject {

    constructor(public workspace: Workspace,
                public repository: Repository) {
    }

    public get expandable(): boolean {
        return true;
    }
}

export class WorkspaceBundleTreeNode implements TreeObject {

    constructor(public bundleFile: BundleFile,
                public workspace: Workspace,
                public repository: Repository) {
    }

    public get expandable(): boolean {
        return true;
    }
}

export class WorkspaceBundleFileEntryTreeNode implements TreeObject {

    constructor(public bundleFileEntry: BundleFileEntry,
                public workspace: Workspace,
                public repository: Repository) {
    }

    public get expandable(): boolean {
        return false;
    }
}

export class WorkspaceTreeObjectDataSource implements TreeObjectDataSource {

    constructor(private workspaceService: WorkspaceService,
                private repository: Repository) {
    }

    getRootObjects(): Observable<TreeObject[]> {
        return this.workspaceService
            .getRepositoryWorkspaces(this.repository.id)
            .pipe(map(workspaces => workspaces.map(workspace => new WorkspaceTreeNode(workspace, this.repository))));
    }

    getChildren(parent: TreeObject, level: number): Observable<TreeObject[]> {
        if (level == 2) {
            const workspaceNode: WorkspaceTreeNode = <WorkspaceTreeNode>parent;

            return this.workspaceService
                .getWorkspaceBundleFiles(workspaceNode.workspace.id)
                .pipe(map(bundleFiles =>
                    _.map(bundleFiles || [], bundleFile => new WorkspaceBundleTreeNode(bundleFile, workspaceNode.workspace, this.repository))
                ));
        } else if (level == 3) {
            const bundleTreeNode: WorkspaceBundleTreeNode = <WorkspaceBundleTreeNode>parent;

            return of(bundleTreeNode.bundleFile.files.map(fileEntry => new WorkspaceBundleFileEntryTreeNode(fileEntry, bundleTreeNode.workspace, this.repository)));
        } else {
            return of([]);
        }
    }
}

@Component({
    selector: 'app-repository-details-workspaces',
    templateUrl: './repository-details-workspaces.component.html',
    styleUrls: ['./repository-details-workspaces.component.css']
})
export class RepositoryDetailsWorkspacesComponent implements OnInit {

    @Input() public repository: Repository;

    public RepositoryStatus = RepositoryStatus;
    public RepositoryDetailsWorkspaceTreeNodeComponent = RepositoryDetailsWorkspaceTreeNodeComponent;

    public workspacesDataSource: TreeObjectDataSource = new EmptyTreeObjectDataSource();
    public initInProgress = false;
    public moreActionInProgress = false;

    constructor(private _repositoryService: RepositoryService,
                private _workspaceService: WorkspaceService,
                private _notificationService: NotificationService,
                private _dialog: MatDialog) {
    }

    public ngOnInit(): void {
        this.workspacesDataSource = new WorkspaceTreeObjectDataSource(this._workspaceService, this.repository);
    }

    public onInitialize() {
        this.initInProgress = true;

        this._repositoryService
            .initializeRepository(this.repository.id)
            .toPromise()
            .then(repository => this.repository = repository)
            .catch(error => {
                console.error('Error while initializing repository.', error);
                this._notificationService.displayErrorMessage('ADMIN.REPOSITORIES.ERROR.INITIALIZE', error);
            })
            .finally(() => this.initInProgress = false);
    }

    public onSynchronize() {
        this.moreActionInProgress = true;

        this._workspaceService
            .synchronize(this.repository.id)
            .toPromise()
            .catch(error => {
                console.error('Error while synchronizing workspaces.', error);
                this._notificationService.displayErrorMessage('ADMIN.WORKSPACES.ERROR.WORKSPACES_SYNCHRONIZE', error);
            })
            .finally(() => this.moreActionInProgress = false);
    }

    public onOpenTranslationsConfig() {
        this._dialog.open(RepositoryDetailsTranslationsConfigurationComponent, {data: {repository: this.repository}});
    }
}
