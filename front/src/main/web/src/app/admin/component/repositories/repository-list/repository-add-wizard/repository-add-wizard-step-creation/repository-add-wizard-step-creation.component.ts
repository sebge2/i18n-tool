import {Component, Input, OnDestroy} from '@angular/core';
import {RepositoryCreationRequestDto} from '../../../../../../api';
import {Repository, RepositoryService} from '@i18n-core-translation';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {FormGroup} from '@angular/forms';

@Component({
    selector: 'app-repository-add-wizard-step-creation',
    templateUrl: './repository-add-wizard-step-creation.component.html',
    styleUrls: ['./repository-add-wizard-step-creation.component.css'],
})
export class RepositoryAddWizardStepCreationComponent implements OnDestroy {
    @Input() public form: FormGroup;

    creationInProgress = false;
    error: any;

    private _creationRequest: RepositoryCreationRequestDto;
    private readonly _destroyed$ = new Subject<void>();

    constructor(private repositoryService: RepositoryService) {
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    @Input()
    get creationRequest(): RepositoryCreationRequestDto {
        return this._creationRequest;
    }

    set creationRequest(value: RepositoryCreationRequestDto) {
        this._creationRequest = value;
        this.repository = null;
        this.error = null;
        this.creationInProgress = false;

        if (this._creationRequest) {
            this.creationInProgress = true;

            this.repositoryService
                .createRepository(this.creationRequest)
                .pipe(takeUntil(this._destroyed$))
                .toPromise()
                .then((repository) => (this.repository = repository))
                .catch((error) => this.error = error)
                .finally(() => (this.creationInProgress = false));
        }
    }

    get repository(): Repository {
        return this.form.controls['repository'].value;
    }

    set repository(repository: Repository) {
        this.form.controls['repository'].setValue(repository);
    }
}
