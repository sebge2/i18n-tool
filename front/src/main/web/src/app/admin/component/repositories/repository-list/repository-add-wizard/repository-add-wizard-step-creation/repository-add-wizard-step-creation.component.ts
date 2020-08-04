import {Component, Input, OnDestroy} from '@angular/core';
import {ErrorMessagesDto, RepositoryCreationRequestDto} from "../../../../../../api";
import {RepositoryService} from "../../../../../../translations/service/repository.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {FormGroup} from "@angular/forms";
import {Repository} from "../../../../../../translations/model/repository.model";
import {instanceOfErrorMessages, instanceOfHttpError} from "../../../../../../core/shared/utils/error-utils";

@Component({
    selector: 'app-repository-add-wizard-step-creation',
    templateUrl: './repository-add-wizard-step-creation.component.html',
    styleUrls: ['./repository-add-wizard-step-creation.component.css']
})
export class RepositoryAddWizardStepCreationComponent implements OnDestroy {

    @Input() public form: FormGroup;

    public creationInProgress = false;
    public unknownError: any;
    public errorMessages: ErrorMessagesDto;

    private _creationRequest: RepositoryCreationRequestDto;
    private _destroyed$ = new Subject<void>();

    constructor(private repositoryService: RepositoryService) {
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    @Input()
    public get creationRequest(): RepositoryCreationRequestDto {
        return this._creationRequest;
    }

    public set creationRequest(value: RepositoryCreationRequestDto) {
        this._creationRequest = value;
        this.repository = null;

        if (this._creationRequest) {
            this.creationInProgress = true;

            this.repositoryService
                .createRepository(this.creationRequest)
                .pipe(takeUntil(this._destroyed$))
                .toPromise()
                .then(repository => this.repository = repository)
                .catch(error => this.handleError(error))
                .finally(() => this.creationInProgress = false);
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
