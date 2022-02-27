import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Repository} from '@i18n-core-translation';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {RepositoryStatus} from '@i18n-core-translation';
import {AuthenticatedUser} from '@i18n-core-auth';
import {AuthenticationService} from '@i18n-core-auth';
import {Subject} from 'rxjs';
import {WorkspaceService} from '@i18n-core-translation';
import {takeUntil} from 'rxjs/operators';
import {Workspace} from '@i18n-core-translation';
import * as _ from 'lodash';
import {UserService} from '@i18n-core-auth';
import {User} from '@i18n-core-auth';
import {MatDialog} from '@angular/material/dialog';
import {WorkspacesStartReviewDialogComponent} from "@i18n-core-translation";

@Component({
    selector: 'app-repository-view-card',
    templateUrl: './repository-view-card.component.html',
    styleUrls: ['./repository-view-card.component.scss'],
})
export class RepositoryViewCardComponent implements OnInit, OnDestroy {
    @Input() repository: Repository;
    @Output() save = new EventEmitter<Repository>();
    @Output() open = new EventEmitter<Repository>();

    readonly form: FormGroup;
    numberReadyWorkspaces: number = 0;
    numberDirtyWorkspaces: number = 0;
    defaultWorkspace: Workspace;
    currentUser: User;

    private _currentAuthenticatedUser: AuthenticatedUser;
    private readonly _destroyed$ = new Subject<void>();

    constructor(
        private _formBuilder: FormBuilder,
        private _dialog: MatDialog,
        private _authenticationService: AuthenticationService,
        private _userService: UserService,
        private _workspaceService: WorkspaceService
    ) {
        this.form = this._formBuilder.group({
            type: this._formBuilder.control('', [Validators.required]),
        });
    }

    ngOnInit(): void {
        this._workspaceService
            .getRepositoryWorkspaces(this.repository.id)
            .pipe(takeUntil(this._destroyed$))
            .subscribe((workspaces) => {
                this.numberReadyWorkspaces = 0;
                this.defaultWorkspace = null;

                workspaces.filter((workspace) => workspace.isInitialized()).forEach(() => this.numberReadyWorkspaces++);
                workspaces.filter((workspace) => workspace.isDirty()).forEach(() => this.numberDirtyWorkspaces++);
                this.defaultWorkspace = _.find(workspaces, (workspace) => workspace.defaultWorkspace);
            });

        this._authenticationService
            .currentAuthenticatedUser()
            .pipe(takeUntil(this._destroyed$))
            .subscribe((currentAuthenticatedUser) => (this._currentAuthenticatedUser = currentAuthenticatedUser));

        this._userService
            .getCurrentUser()
            .pipe(takeUntil(this._destroyed$))
            .subscribe((currentUser) => (this.currentUser = currentUser));
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    onOpen() {
        this.open.emit(this.repository);
    }

    onPublish() {
        this._dialog.open(WorkspacesStartReviewDialogComponent, {data: {repository: this.repository}});
    }

    get statusIconClass(): string {
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

    get statusLabelKey(): string {
        return `ADMIN.REPOSITORIES.VIEW_CARD.STATUS_VALUE_LABEL.${this.repository.status}`;
    }

    get hasAccess(): boolean {
        return this._currentAuthenticatedUser.hasRepositoryAccess(this.repository.id);
    }
}
