import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Repository} from "../../../../../translations/model/repository/repository.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RepositoryStatus} from "../../../../../translations/model/repository/repository-status.model";
import {AuthenticatedUser} from "../../../../../core/auth/model/authenticated-user.model";
import {AuthenticationService} from "../../../../../core/auth/service/authentication.service";
import {Subject} from "rxjs";
import {WorkspaceService} from "../../../../../translations/service/workspace.service";
import {takeUntil} from "rxjs/operators";
import {Workspace} from "../../../../../translations/model/workspace/workspace.model";
import * as _ from "lodash";
import {UserService} from "../../../../../core/auth/service/user.service";
import {User} from "../../../../../core/auth/model/user.model";
import {WorkspacesStartReviewDialogComponent} from "../../../../../core/translation/component/workspaces-start-review-dialog/workspaces-start-review-dialog.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
    selector: 'app-repository-view-card',
    templateUrl: './repository-view-card.component.html',
    styleUrls: ['./repository-view-card.component.scss']
})
export class RepositoryViewCardComponent implements OnInit, OnDestroy {

    @Input() public repository: Repository;
    @Output() public save = new EventEmitter<Repository>();
    @Output() public open = new EventEmitter<Repository>();

    public readonly form: FormGroup;
    public numberReadyWorkspaces: number = 0;
    public defaultWorkspace: Workspace;
    public currentUser: User;

    private _currentAuthenticatedUser: AuthenticatedUser;
    private _destroyed$ = new Subject<void>();

    constructor(private _formBuilder: FormBuilder,
                private _dialog: MatDialog,
                private _authenticationService: AuthenticationService,
                private _userService: UserService,
                private _workspaceService: WorkspaceService) {
        this.form = this._formBuilder.group(
            {
                type: this._formBuilder.control('', [Validators.required])
            }
        );
    }

    public ngOnInit(): void {
        this._workspaceService
            .getRepositoryWorkspaces(this.repository.id)
            .pipe(takeUntil(this._destroyed$))
            .subscribe(workspaces => {
                this.numberReadyWorkspaces = 0;
                this.defaultWorkspace = null;

                workspaces.filter(workspace => workspace.isInitialized()).forEach(() => this.numberReadyWorkspaces++);
                this.defaultWorkspace = _.find(workspaces, workspace => workspace.defaultWorkspace);
            });

        this._authenticationService
            .currentAuthenticatedUser()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(currentAuthenticatedUser => this._currentAuthenticatedUser = currentAuthenticatedUser);

        this._userService
            .getCurrentUser()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(currentUser => this.currentUser = currentUser);
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public onOpen() {
        this.open.emit(this.repository);
    }

    public onPublish() {
        this._dialog.open(WorkspacesStartReviewDialogComponent, {data: {repository: this.repository}});
    }

    public get statusIconClass(): string {
        switch (this.repository.status) {
            case RepositoryStatus.NOT_INITIALIZED:
                return 'status-warning';
            case RepositoryStatus.INITIALIZATION_ERROR:
                return 'status-error';
            case RepositoryStatus.INITIALIZING:
                return 'status-progress';
            case RepositoryStatus.INITIALIZED:
                return 'status-success';
            default:
                return '';
        }
    }

    public get statusLabelKey(): string {
        return `ADMIN.REPOSITORIES.VIEW_CARD.STATUS_VALUE_LABEL.${this.repository.status}`;
    }

    public get hasAccess(): boolean {
        return this._currentAuthenticatedUser.hasRepositoryAccess(this.repository.id);
    }
}
