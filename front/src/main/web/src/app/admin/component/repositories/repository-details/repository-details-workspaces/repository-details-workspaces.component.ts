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
import {interval, Observable, of} from "rxjs";
import {Workspace} from "../../../../../translations/model/workspace/workspace.model";
import {WorkspaceService} from "../../../../../translations/service/workspace.service";
import {map} from "rxjs/operators";
import {RepositoryDetailsWorkspaceNodeComponent} from "./repository-details-workspace-node/repository-details-workspace-node.component";
import {BundleFile} from "../../../../../translations/model/workspace/bundle-file.model";
import {BundleFileEntry} from "../../../../../translations/model/workspace/bundle-file-entry.model";

export class WorkspaceTreeNode implements TreeObject {

    constructor(public workspace: Workspace) {
    }

    public get expandable(): boolean {
        return true;
    }

    public get name(): string {
        return this.workspace.branch;
    }
}

export class WorkspaceBundleTreeNode implements TreeObject {

    constructor(public bundleFile: BundleFile) {
    }

    public get expandable(): boolean {
        return true;
    }

    public get name(): string {
        return `${this.bundleFile.location}/${this.bundleFile.name}`;
    }
}

export class WorkspaceBundleFileEntryTreeNode implements TreeObject {

    constructor(public bundleFileEntry: BundleFileEntry) {
    }

    public get expandable(): boolean {
        return false;
    }

    public get name(): string {
        return this.bundleFileEntry.file;
    }
}

export class WorkspaceTreeObjectDataSource implements TreeObjectDataSource {

    constructor(private workspaceService: WorkspaceService,
                private repository: Repository) {
    }

    getRootObjects(): Observable<TreeObject[]> {
        return this.workspaceService
            .getRepositoryWorkspaces(this.repository.id)
            .pipe(map(workspaces => workspaces.map(workspace => new WorkspaceTreeNode(workspace))));
    }

    getChildren(parent: TreeObject, level: number): Observable<TreeObject[]> {
        if(level == 2){
            const workspaceNode :WorkspaceTreeNode = <WorkspaceTreeNode> parent;

            return this.workspaceService
                .getWorkspaceBundleFile(workspaceNode.workspace.id)
                .pipe(map(bundleFiles => bundleFiles.map(bundleFile => new WorkspaceBundleTreeNode(bundleFile))));
        } else if(level == 3){
            const bundleTreeNode :WorkspaceBundleTreeNode = <WorkspaceBundleTreeNode> parent;

            return of(bundleTreeNode.bundleFile.files.map(fileEntry => new WorkspaceBundleFileEntryTreeNode(fileEntry)));
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
    public RepositoryDetailsWorkspaceNodeComponent = RepositoryDetailsWorkspaceNodeComponent;

    public workspacesDataSource: TreeObjectDataSource = new EmptyTreeObjectDataSource();
    public initInProgress = false;

    constructor(private _repositoryService: RepositoryService,
                private _workspaceService: WorkspaceService,
                private _notificationService: NotificationService) {
    }

    ngOnInit(): void {
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
}
