import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {ErrorMessagesDto, GitRepositoryPatchRequestDto} from "../../../../../../api";
import {Subject} from "rxjs";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Repository} from "../../../../../../translations/model/repository/repository.model";
import {RepositoryService} from "../../../../../../translations/service/repository.service";
import {filter, takeUntil} from "rxjs/operators";
import {instanceOfErrorMessages, instanceOfHttpError} from "../../../../../../core/shared/utils/error-utils";
import {getStringValue} from "../../../../../../core/shared/utils/form-utils";

@Component({
    selector: 'app-repository-git-credentials-dialog',
    templateUrl: './repository-git-credentials-dialog.component.html',
    styleUrls: ['./repository-git-credentials-dialog.component.css']
})
export class RepositoryGitCredentialsDialogComponent implements OnInit, OnDestroy {

    public readonly form: FormGroup;

    public saveInProgress: boolean = false;
    public deleteInProgress: boolean = false;

    public unknownError: any;

    public errorMessages: ErrorMessagesDto;
    private _destroyed$ = new Subject<void>();

    constructor(private _dialogRef: MatDialogRef<RepositoryGitCredentialsDialogComponent>,
                private _formBuilder: FormBuilder,
                @Inject(MAT_DIALOG_DATA) public data: { repository: Repository },
                private _repositoryService: RepositoryService) {
        this.form = _formBuilder.group({
            username: [],
            password: [],
        });
    }

    public ngOnInit(): void {
        this._repositoryService
            .getRepository(this.data.repository.id)
            .pipe(
                takeUntil(this._destroyed$),
                filter(repository => !repository),
            )
            .subscribe(() => this._dialogRef.close());
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public get failed(): boolean {
        return this.unknownError || this.errorMessages;
    }

    public onSave() {
        this.saveInProgress = true;

        this._repositoryService
            .updateRepository(this.data.repository.id, <GitRepositoryPatchRequestDto>{
                id: this.data.repository.id,
                type: "GIT",
                username: getStringValue(this.form.controls['username']),
                password: getStringValue(this.form.controls['password'])
            })
            .toPromise()
            .then(() => this._dialogRef.close())
            .catch(error => this.handleError(error))
            .finally(() => this.saveInProgress = false);
    }

    public onGoBack() {
        this.errorMessages = null;
        this.unknownError = null;
    }

    private handleError(cause: any) {
        if (instanceOfHttpError(cause)) {
            return this.handleError(cause.error);
        } else if (instanceOfErrorMessages(cause)) {
            this.errorMessages = <ErrorMessagesDto>cause;
        } else {
            this.unknownError = cause;
        }
    }
}
