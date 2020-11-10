import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ErrorMessagesDto, GitHubRepositoryPatchRequestDto} from "../../../../../../api";
import {Subject} from "rxjs";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Repository} from "../../../../../../translations/model/repository/repository.model";
import {RepositoryService} from "../../../../../../translations/service/repository.service";
import {filter, takeUntil} from "rxjs/operators";
import {instanceOfErrorMessages, instanceOfHttpError} from "../../../../../../core/shared/utils/error-utils";

@Component({
    selector: 'app-repository-github-access-key-dialog',
    templateUrl: './repository-github-access-key-dialog.component.html',
    styleUrls: ['./repository-github-access-key-dialog.component.css']
})
export class RepositoryGithubAccessKeyDialogComponent implements OnInit, OnDestroy {

    public readonly form: FormGroup;

    public saveInProgress: boolean = false;
    public deleteInProgress: boolean = false;

    public unknownError: any;

    public errorMessages: ErrorMessagesDto;
    private _destroyed$ = new Subject<void>();

    constructor(private _dialogRef: MatDialogRef<RepositoryGithubAccessKeyDialogComponent>,
                private _formBuilder: FormBuilder,
                @Inject(MAT_DIALOG_DATA) public data: { repository: Repository },
                private _repositoryService: RepositoryService) {
        this.form = _formBuilder.group({
            accessKey: ['', Validators.required],
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
            .updateRepository(this.data.repository.id, <GitHubRepositoryPatchRequestDto>{
                id: this.data.repository.id,
                type: "GITHUB",
                accessKey: this.form.controls['accessKey'].value
            })
            .toPromise()
            .then(() => this._dialogRef.close())
            .catch(error => this.handleError(error))
            .finally(() => this.saveInProgress = false);
    }

    public onDelete() {
        this.deleteInProgress = true;

        this._repositoryService
            .updateRepository(this.data.repository.id, <GitHubRepositoryPatchRequestDto>{
                id: this.data.repository.id,
                type: "GITHUB",
                accessKey: ''
            })
            .toPromise()
            .then(() => this._dialogRef.close())
            .catch(error => this.handleError(error))
            .finally(() => this.deleteInProgress = false);
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
