import {Component, Input} from '@angular/core';
import {Repository} from "../../../../../translations/model/repository/repository.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RepositoryStatus} from "../../../../../translations/model/repository/repository-status.model";
import {RepositoryService} from "../../../../../translations/service/repository.service";
import {NotificationService} from "../../../../../core/notification/service/notification.service";
import {GitRepository} from "../../../../../translations/model/repository/git-repository.model";
import {GitHubRepository} from "../../../../../translations/model/repository/github-repository.model";
import {RepositoryType} from "../../../../../translations/model/repository/repository-type.model";
import {
    GitHubRepositoryPatchRequestDto,
    GitRepositoryPatchRequestDto,
    RepositoryPatchRequestDto
} from "../../../../../api";
import {MatDialog} from "@angular/material/dialog";
import {RepositoryGithubWebHookDialogComponent} from "./repository-github-web-hook-dialog/repository-github-web-hook-dialog.component";
import {RepositoryGithubAccessKeyDialogComponent} from "./repository-github-access-key-dialog/repository-github-access-key-dialog.component";
import {RepositoryGitCredentialsDialogComponent} from "./repository-git-credentials-dialog/repository-git-credentials-dialog.component";

@Component({
    selector: 'app-repository-details-config',
    templateUrl: './repository-details-config.component.html',
    styleUrls: ['./repository-details-config.component.css']
})
export class RepositoryDetailsConfigComponent {

    public readonly form: FormGroup;
    public readonly RepositoryType = RepositoryType;

    public cancelInProgress: boolean = false;
    public deleteInProgress: boolean = false;
    public saveInProgress: boolean = false;

    private _repository: Repository;

    constructor(private _formBuilder: FormBuilder,
                private _repositoryService: RepositoryService,
                private _notificationService: NotificationService,
                private _dialog: MatDialog) {
        this.form = this._formBuilder.group({});
    }

    @Input()
    public get repository(): Repository {
        return this._repository;
    }

    public set repository(repository: Repository) {
        this._repository = repository;

        switch (this.repository.type) {
            case "GIT":
                this.form.registerControl('name', this._formBuilder.control('', [Validators.required]));
                this.form.registerControl('location', this._formBuilder.control('', [Validators.required]));
                this.form.registerControl('defaultBranch', this._formBuilder.control('', [Validators.required]));
                this.form.registerControl('allowedBranches', this._formBuilder.control('', [Validators.required]));

                break;
            case "GITHUB":
                this.form.registerControl('name', this._formBuilder.control('', [Validators.required]));
                this.form.registerControl('defaultBranch', this._formBuilder.control('', [Validators.required]));
                this.form.registerControl('allowedBranches', this._formBuilder.control('', [Validators.required]));

                break;
            default:
                throw new Error(`Unsupported type ${this.repository.type}.`)
        }

        this.resetForm();
    }

    public get deleteAllowed(): boolean {
        return this.repository.status !== RepositoryStatus.INITIALIZING;
    }

    public get actionInProgress(): boolean {
        return this.cancelInProgress || this.saveInProgress || this.deleteInProgress;
    }

    public onCancel() {
        this.cancelInProgress = true;
        this.resetForm();
        this.cancelInProgress = false;
    }

    public onSave() {
        this.saveInProgress = true;

        this._repositoryService
            .updateRepository(this.repository.id, this.createPatch())
            .toPromise()
            .then(repository => this.repository = repository)
            .catch(error => this._notificationService.displayErrorMessage('ADMIN.REPOSITORIES.ERROR.UPDATE', error))
            .finally(() => this.saveInProgress = false);
    }

    public onDelete() {
        this.deleteInProgress = true;

        this._repositoryService
            .deleteRepository(this.repository)
            .toPromise()
            .catch(error => this._notificationService.displayErrorMessage('ADMIN.REPOSITORIES.ERROR.DELETE', error))
            .finally(() => this.deleteInProgress = false);
    }

    public onUpdateGitHubWebHookSecret() {
        this._dialog.open(RepositoryGithubWebHookDialogComponent, {data: {repository: this.repository}});
    }

    public onUpdateGitHubAccessKey() {
        this._dialog.open(RepositoryGithubAccessKeyDialogComponent, {data: {repository: this.repository}});
    }

    public onUpdateGitCredentials(){
        this._dialog.open(RepositoryGitCredentialsDialogComponent, {data: {repository: this.repository}});
    }

    private resetForm() {
        switch (this.repository.type) {
            case "GIT":
                const gitRepository = <GitRepository>this.repository;

                this.form.controls['name'].setValue(gitRepository.name);

                this.form.controls['location'].setValue(gitRepository.location);
                this.form.controls['location'].disable();

                this.form.controls['defaultBranch'].setValue(gitRepository.defaultBranch);
                this.form.controls['allowedBranches'].setValue(gitRepository.allowedBranches);

                break;
            case "GITHUB":
                const gitHubRepository = <GitHubRepository>this.repository;

                this.form.controls['name'].setValue(gitHubRepository.name);
                this.form.controls['name'].disable();

                this.form.controls['defaultBranch'].setValue(gitHubRepository.defaultBranch);
                this.form.controls['allowedBranches'].setValue(gitHubRepository.allowedBranches);

                break;
            default:
                throw new Error(`Unsupported type ${this.repository.type}.`)
        }

        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

    private createPatch(): RepositoryPatchRequestDto {
        switch (this.repository.type) {
            case "GIT":
                return <GitRepositoryPatchRequestDto>{
                    id: this.repository.id,
                    type: this.repository.type,
                    name: this.form.controls['name'].value,
                    defaultBranch: this.form.controls['defaultBranch'].value,
                    allowedBranches: this.form.controls['allowedBranches'].value
                };
            case "GITHUB":
                return <GitHubRepositoryPatchRequestDto>{
                    id: this.repository.id,
                    type: this.repository.type,
                    accessKey: null,
                    webHookSecret: null,
                    defaultBranch: this.form.controls['defaultBranch'].value,
                    allowedBranches: this.form.controls['allowedBranches'].value
                };
            default:
                throw new Error(`Unsupported type ${this.repository.type}.`)
        }
    }
}
