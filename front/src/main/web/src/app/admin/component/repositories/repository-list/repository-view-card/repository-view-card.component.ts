import {Component, EventEmitter, Input, OnDestroy, Output} from '@angular/core';
import {Repository} from "../../../../../translations/model/repository.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RepositoryType} from "../../../../../translations/model/repository-type.model";
import {RepositoryStatus} from "../../../../../translations/model/repository-status.model";
import {AuthenticatedUser} from "../../../../../core/auth/model/authenticated-user.model";
import {AuthenticationService} from "../../../../../core/auth/service/authentication.service";
import {Observable, Subject} from "rxjs";
import {RepositoryService} from "../../../../../translations/service/repository.service";
import {takeUntil} from "rxjs/operators";
import {NotificationService} from "../../../../../core/notification/service/notification.service";

@Component({
    selector: 'app-repository-view-card',
    templateUrl: './repository-view-card.component.html',
    styleUrls: ['./repository-view-card.component.css']
})
export class RepositoryViewCardComponent implements OnDestroy {

    @Input() public repository: Repository;
    @Output() public save = new EventEmitter<Repository>();
    @Output() public open = new EventEmitter<Repository>();

    public readonly form: FormGroup;
    public readonly repositoryStatus = RepositoryStatus;
    public readonly types = [RepositoryType.GIT, RepositoryType.GITHUB];

    public readonly currentAuthenticatedUser: Observable<AuthenticatedUser> = this.authenticationService.currentAuthenticatedUser();
    public initializeInProgress : boolean = false;

    private _destroyed$ = new Subject<void>();

    constructor(private formBuilder: FormBuilder,
                private authenticationService: AuthenticationService,
                private repositoryService: RepositoryService,
                private notificationService: NotificationService) {
        this.form = this.formBuilder.group(
            {
                type: this.formBuilder.control('', [Validators.required])
            }
        );
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public onOpen() {
        this.open.emit(this.repository);
    }

    public get initializeAllowed(): boolean {
        return this.repository.status == RepositoryStatus.NOT_INITIALIZED;
    }

    public onInitialize() {
        this.initializeInProgress = true;
        this.repositoryService
            .initializeRepository(this.repository.id)
            .pipe(takeUntil(this._destroyed$))
            .toPromise()
            .catch(error => this.notificationService.displayErrorMessage('ADMIN.REPOSITORIES.VIEW_CARD.ERROR.INITIALIZE', error))
            .finally(() => this.initializeInProgress = false);
    }
}
