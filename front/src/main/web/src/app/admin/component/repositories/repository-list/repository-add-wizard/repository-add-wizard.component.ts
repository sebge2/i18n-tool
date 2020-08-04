import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RepositoryType} from "../../../../../translations/model/repository-type.model";
import {StepChangeEvent, WizardComponent} from "../../../../../core/shared/component/wizard/wizard.component";
import {
    GitHubRepositoryCreationRequestDto,
    GitRepositoryCreationRequestDto,
    RepositoryCreationRequestDto
} from "../../../../../api";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {Repository} from "../../../../../translations/model/repository.model";

@Component({
    selector: 'app-repository-add-wizard',
    templateUrl: './repository-add-wizard.component.html',
    styleUrls: ['./repository-add-wizard.component.css']
})
export class RepositoryAddWizardComponent implements OnInit, OnDestroy {

    public form: FormGroup;
    public repositoryType: RepositoryType;
    public creationRequest: RepositoryCreationRequestDto;
    public createdRepository: Repository;

    @ViewChild('wizard', {static: true}) private wizard: WizardComponent;

    private static STEP_CONFIG = 1;
    private static STEP_CREATION = 2;
    private static STEP_INITIALIZATION = 3;

    private static FORM_STEP_TYPE = 0;
    private static FORM_STEP_NO_TYPE_CONFIG = 1;
    private static FORM_STEP_GITHUB_CONFIG = 2;
    private static FORM_STEP_GIT_CONFIG = 3;
    private static FORM_STEP_CREATION = 4;
    private static FORM_STEP_INITIALIZATION = 5;

    private _destroyed$ = new Subject<void>();

    constructor(private formBuilder: FormBuilder) {
        this.form = this.formBuilder.group({
            stepsForm: this.formBuilder.array([
                this.formBuilder.group({type: this.formBuilder.control('', [Validators.required])}), // step type

                this.formBuilder.group({}), // step config no type selected

                this.formBuilder.group({
                    username: this.formBuilder.control('', [Validators.required]),
                    repository: this.formBuilder.control('', [Validators.required]),
                    accessKey: this.formBuilder.control('', [])
                }), // step repo config github

                this.formBuilder.group({
                    location: this.formBuilder.control('', [Validators.required]),
                    name: this.formBuilder.control('', [Validators.required])
                }), // step repo config git

                this.formBuilder.group({
                    repository: this.formBuilder.control(null, [Validators.required])
                }), // step repo creation

                this.formBuilder.group({
                    repository: this.formBuilder.control(null, [Validators.required])
                }) // step repo initialization
            ])
        });
    }

    public ngOnInit(): void {
        this.stepTypeForm.statusChanges
            .pipe(takeUntil(this._destroyed$))
            .subscribe(_ => {
                if (this.stepTypeForm.valid) {
                    this.wizard.nextStep()
                }
            });
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public get stepTypeEditable(): boolean {
        return !this.repositoryType;
    }

    public get stepTypeForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(RepositoryAddWizardComponent.FORM_STEP_TYPE);
    }

    public get stepConfigForm(): FormGroup {
        let index;
        if (!this.repositoryType) {
            index = RepositoryAddWizardComponent.FORM_STEP_NO_TYPE_CONFIG;
        } else if (this.repositoryType === RepositoryType.GITHUB) {
            index = RepositoryAddWizardComponent.FORM_STEP_GITHUB_CONFIG;
        } else if (this.repositoryType === RepositoryType.GIT) {
            index = RepositoryAddWizardComponent.FORM_STEP_GIT_CONFIG;
        }

        return <FormGroup>this.stepsForm.at(index);
    }

    public get stepConfigEditable(): boolean {
        return this.stepCreationEditable;
    }

    public get stepCreationForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(RepositoryAddWizardComponent.FORM_STEP_CREATION);
    }

    public get stepCreationEditable(): boolean {
        return !this.stepCreationForm.valid;
    }

    public get stepInitializationForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(RepositoryAddWizardComponent.FORM_STEP_INITIALIZATION);
    }

    public get stepInitializationEditable(): boolean {
        return !this.stepInitializationForm.valid;
    }

    public onStepChange(stepChangeEvent: StepChangeEvent) {
        if (stepChangeEvent.nextStepIndex == RepositoryAddWizardComponent.STEP_CONFIG) {
            this.repositoryType = this.stepTypeForm.controls['type'].value;
        } else if (stepChangeEvent.nextStepIndex == RepositoryAddWizardComponent.STEP_CREATION) {
            this.creationRequest = this.createRequest();
        }else if (stepChangeEvent.nextStepIndex == RepositoryAddWizardComponent.STEP_INITIALIZATION) {
            this.createdRepository = this.stepCreationForm.controls['repository'].value;
        }
    }

    private createRequest(): RepositoryCreationRequestDto | null {
        if (!this.repositoryType) {
            return null;
        } else if (this.repositoryType == RepositoryType.GIT) {
            return <GitRepositoryCreationRequestDto>{
                type: "GIT",
                location: this.stepConfigForm.controls['location'].value,
                name: this.stepConfigForm.controls['name'].value
            };
        } else if (this.repositoryType == RepositoryType.GITHUB) {
            return <GitHubRepositoryCreationRequestDto>{
                type: "GITHUB",
                username: this.stepConfigForm.controls['username'].value,
                repository: this.stepConfigForm.controls['repository'].value,
                accessKey: this.stepConfigForm.controls['accessKey'].value,
            };
        } else {
            throw Error(`Unsupported repository type ${this.repositoryType}.`);
        }
    }

    private get stepsForm(): FormArray | null {
        return <FormArray>this.form.controls['stepsForm'];
    }
}
