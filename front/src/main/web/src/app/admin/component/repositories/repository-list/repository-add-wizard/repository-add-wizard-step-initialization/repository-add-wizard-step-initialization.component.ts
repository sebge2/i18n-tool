import {Component, Input, OnDestroy} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {ErrorMessagesDto} from "../../../../../../api";
import {Subject} from "rxjs";
import {instanceOfErrorMessages, instanceOfHttpError} from "../../../../../../core/shared/utils/error-utils";
import {Repository} from "../../../../../../translations/model/repository/repository.model";
import {takeUntil} from "rxjs/operators";
import {RepositoryService} from "../../../../../../translations/service/repository.service";

@Component({
    selector: 'app-repository-add-wizard-step-initialization',
    templateUrl: './repository-add-wizard-step-initialization.component.html',
    styleUrls: ['./repository-add-wizard-step-initialization.component.css']
})
export class RepositoryAddWizardStepInitializationComponent implements OnDestroy {

    @Input() public form: FormGroup;

    public initializationInProgress = false;
    public unknownError: any;
    public errorMessages: ErrorMessagesDto;

    private _originalRepository: Repository;
    private _destroyed$ = new Subject<void>();

    constructor(private repositoryService: RepositoryService) {
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    @Input()
    public get originalRepository(): Repository {
        return this._originalRepository;
    }

    public set originalRepository(value: Repository) {
        this._originalRepository = value;
        this.repository = null;
        this.unknownError = null;
        this.errorMessages = null;

        if (this._originalRepository) {
            this.initializationInProgress = true;

            this.repositoryService
                .initializeRepository(this.originalRepository.id)
                .pipe(takeUntil(this._destroyed$))
                .toPromise()
                .then(repository => this.repository = repository)
                .catch(error => this.handleError(error))
                .finally(() => this.initializationInProgress = false);
        }
    }

    public get repository(): Repository {
        return this.form.controls['repository'].value;
    }

    public set repository(repository: Repository) {
        this.form.controls['repository'].setValue(repository);
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
