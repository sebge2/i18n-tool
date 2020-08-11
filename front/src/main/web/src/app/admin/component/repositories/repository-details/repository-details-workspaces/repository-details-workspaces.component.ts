import {Component, Input, OnInit} from '@angular/core';
import {Repository} from "../../../../../translations/model/repository/repository.model";
import {RepositoryStatus} from "../../../../../translations/model/repository/repository-status.model";
import {RepositoryService} from "../../../../../translations/service/repository.service";
import {NotificationService} from "../../../../../core/notification/service/notification.service";
import {
    EmptyTreeObjectDataSource, TreeObject,
    TreeObjectDataSource
} from "../../../../../core/shared/component/tree/tree.component";
import {interval, Observable, of, timer} from "rxjs";
import {BundleFileDto} from "../../../../../api";
import {Workspace} from "../../../../../translations/model/workspace.model";
import {WorkspaceService} from "../../../../../translations/service/workspace.service";
import {map} from "rxjs/operators";

export class WorkspaceTreeNode implements TreeObject {

    constructor(private workspace: Workspace) {
    }

    public get expandable(): boolean{
        return true;
    }

    public get name(): string {
        return this.workspace.branch;
    }

}

export class WorkspaceBundleTreeNode implements TreeObject {

    constructor(private bundleFileDto: BundleFileDto) {
    }

    public get expandable(): boolean{
        return true;
    }

    public get name(): string {
        return this.bundleFileDto.location;
    }
}

export class WorkspaceBundleFileEntryTreeNode implements TreeObject {

    constructor() {
    }

    public get expandable(): boolean{
        return false;
    }

    public get name(): string {
        return '';
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

    getChildren(parent: TreeObject): Observable<TreeObject[]> {
        return interval(1000)
            .pipe(map(index =>
                [
                    new WorkspaceBundleTreeNode({
                        location: "/tmp/test" + index,
                        type: "JAVA_PROPERTIES",
                        name: "test",
                        id: 'toto'
                    })
                ]
            ))
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
