import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormGroup} from "@angular/forms";
import {Repository} from "../../../../../../translations/model/repository/repository.model";
import {RepositoryService} from "../../../../../../translations/service/repository.service";
import {ErrorMessagesDto, RepositoryPatchRequestDto} from "../../../../../../api";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {instanceOfErrorMessages, instanceOfHttpError} from "../../../../../../core/shared/utils/error-utils";
import {RepositoryDetailsTranslationsGlobalConfigurationComponent} from "./repository-details-translations-global-configuration/repository-details-translations-global-configuration.component";
import {RepositoryDetailsTranslationsBundleConfigurationComponent} from "./repository-details-translations-bundle-configuration/repository-details-translations-bundle-configuration.component";

@Component({
    selector: 'app-repository-details-translations-configuration',
    templateUrl: './repository-details-translations-configuration.component.html',
    styleUrls: ['./repository-details-translations-configuration.component.css']
})
export class RepositoryDetailsTranslationsConfigurationComponent implements OnInit, OnDestroy {

    public readonly form: FormGroup;
    public repository: Repository;

    public saveInProgress: boolean = false;

    public unknownError: any;

    public errorMessages: ErrorMessagesDto;
    private _destroyed$ = new Subject<void>();

    constructor(private _dialogRef: MatDialogRef<RepositoryDetailsTranslationsConfigurationComponent>,
                private _formBuilder: FormBuilder,
                @Inject(MAT_DIALOG_DATA) public data: { repository: Repository },
                private _repositoryService: RepositoryService) {
        this.form = _formBuilder.group({
            global: _formBuilder.group({}),
            javaProperties: _formBuilder.group({}),
            jsonIcu: _formBuilder.group({})
        });
    }

    public ngOnInit(): void {
        this._repositoryService
            .getRepository(this.data.repository.id)
            .pipe(takeUntil(this._destroyed$))
            .subscribe(repository => {
                if (repository) {
                    this.repository = repository;
                } else {
                    this._dialogRef.close()
                }
            });
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public get failed(): boolean {
        return this.unknownError || this.errorMessages;
    }

    public get globalForm(): FormGroup {
        return <FormGroup>this.form.controls['global'];
    }

    public get javaPropertiesForm(): FormGroup {
        return <FormGroup>this.form.controls['javaProperties'];
    }

    public get jsonIcuForm(): FormGroup {
        return <FormGroup>this.form.controls['jsonIcu'];
    }

    public onSave() {
        this.saveInProgress = true;

        this._repositoryService
            .updateRepository(this.data.repository.id, <RepositoryPatchRequestDto>{
                id: this.data.repository.id,
                type: this.data.repository.type,
                translationsConfiguration: {
                    ignoredKeys: RepositoryDetailsTranslationsGlobalConfigurationComponent.getIgnoredProperties(this.globalForm),
                    javaProperties: {
                        ignoredPaths: RepositoryDetailsTranslationsBundleConfigurationComponent.getIgnoredPaths(this.javaPropertiesForm),
                    },
                    jsonIcu: {
                        ignoredPaths: RepositoryDetailsTranslationsBundleConfigurationComponent.getIgnoredPaths(this.jsonIcuForm),
                    }
                }
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
