import {Component, Input} from '@angular/core';
import {Repository} from "../../../../../translations/model/repository/repository.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RepositoryStatus} from "../../../../../translations/model/repository/repository-status.model";
import {RepositoryService} from "../../../../../translations/service/repository.service";
import {NotificationService} from "../../../../../core/notification/service/notification.service";
import {GitRepository} from "../../../../../translations/model/repository/git-repository.model";
import {GitHubRepository} from "../../../../../translations/model/repository/github-repository.model";
import {RepositoryType} from "../../../../../translations/model/repository/repository-type.model";

@Component({
    selector: 'app-repository-details-config',
    templateUrl: './repository-details-config.component.html',
    styleUrls: ['./repository-details-config.component.css']
})
export class RepositoryDetailsConfigComponent {

    public readonly form: FormGroup;
    public readonly RepositoryType = RepositoryType;

    public initializeInProgress: boolean = false;
    public cancelInProgress: boolean = false;
    public deleteInProgress: boolean = false;
    public saveInProgress: boolean = false;

    private _repository: Repository;

    constructor(private formBuilder: FormBuilder,
                private repositoryService: RepositoryService,
                private notificationService: NotificationService) {
        this.form = this.formBuilder.group({});
    }

    @Input()
    public get repository(): Repository {
        return this._repository;
    }

    public set repository(repository: Repository) {
        this._repository = repository;

        switch (this.repository.type) {
            case "GIT":
                this.form.registerControl('name', this.formBuilder.control('', [Validators.required]));
                this.form.registerControl('location', this.formBuilder.control('', [Validators.required]));
                this.form.registerControl('defaultBranch', this.formBuilder.control('', [Validators.required]));

                break;
            case "GITHUB":
                this.form.registerControl('name', this.formBuilder.control('', [Validators.required]));
                this.form.registerControl('defaultBranch', this.formBuilder.control('', [Validators.required]));
                this.form.registerControl('accessKey', this.formBuilder.control('', []));
                this.form.registerControl('webHookSecret', this.formBuilder.control('', []));

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
        return this.initializeInProgress || this.cancelInProgress || this.saveInProgress || this.deleteInProgress;
    }

    public get initializeAllowed(): boolean {
        return this.repository.status == RepositoryStatus.NOT_INITIALIZED;
    }

    public onInitialize() {
        this.initializeInProgress = true;
        this.repositoryService
            .initializeRepository(this.repository.id)
            .toPromise()
            .catch(error => this.notificationService.displayErrorMessage('ADMIN.REPOSITORIES.VIEW_CARD.ERROR.INITIALIZE', error))
            .finally(() => this.initializeInProgress = false);
    }

    public onCancel() {
        this.cancelInProgress = true;
        this.resetForm();
        this.cancelInProgress = false;
    }

    public onSave() {
        this.saveInProgress = true;

        // if (this.isExistingUser()) {
        //     this.userService
        //         .updateUser(this.user.id, this.toUpdatedUser())
        //         .toPromise()
        //         .then(user => this.user = user)
        //         .then(user => this.save.emit(user))
        //         .catch(error => this.notificationService.displayErrorMessage('ADMIN.USERS.ERROR.UPDATE', error))
        //         .finally(() => this.saveInProgress = false);
        // } else {
        //     this.userService
        //         .createUser(this.toNewUser())
        //         .toPromise()
        //         .then(user => this.user = user)
        //         .then(user => this.save.emit(user))
        //         .catch(error => this.notificationService.displayErrorMessage('ADMIN.USERS.ERROR.SAVE', error))
        //         .finally(() => this.saveInProgress = false);
        // }
    }

    public onDelete() {
        // if (this.user.id) {
        //     this.deleteInProgress = true;
        //     this.userService
        //         .deleteUser(this.user.id)
        //         .toPromise()
        //         .catch(error => this.notificationService.displayErrorMessage('ADMIN.USERS.ERROR.DELETE', error))
        //         .finally(() => this.deleteInProgress = false);
        // } else {
        //     this.delete.emit();
        // }
    }

    private resetForm() {
        switch (this.repository.type) {
            case "GIT":
                const gitRepository = <GitRepository>this.repository;

                this.form.controls['name'].setValue(gitRepository.name);

                this.form.controls['location'].setValue(gitRepository.location);
                this.form.controls['location'].disable();

                this.form.controls['defaultBranch'].setValue(gitRepository.defaultBranch);

                break;
            case "GITHUB":
                const gitHubRepository = <GitHubRepository>this.repository;

                this.form.controls['name'].setValue(gitHubRepository.name);
                this.form.controls['name'].disable();

                this.form.controls['defaultBranch'].setValue(gitHubRepository.defaultBranch);
                this.form.controls['accessKey'].setValue(gitHubRepository.accessKey);
                this.form.controls['webHookSecret'].setValue(gitHubRepository.webHookSecret);

                break;
            default:
                throw new Error(`Unsupported type ${this.repository.type}.`)
        }

        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

}
