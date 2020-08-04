import {Component, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RepositoryType} from "../../../../../translations/model/repository-type.model";
import {StepChangeEvent, WizardComponent} from "../../../../../core/shared/component/wizard/wizard.component";
import {
    GitHubRepositoryCreationRequestDto,
    GitRepositoryCreationRequestDto,
    RepositoryCreationRequestDto
} from "../../../../../api";

@Component({
    selector: 'app-repository-add-wizard',
    templateUrl: './repository-add-wizard.component.html',
    styleUrls: ['./repository-add-wizard.component.css']
})
export class RepositoryAddWizardComponent {

    @ViewChild('wizard', {static: true}) wizard: WizardComponent;

    public form: FormGroup;
    public creationDto: RepositoryCreationRequestDto = null;
    public RepositoryType = RepositoryType;

    private static STEP_TYPE = 0;
    private static STEP_NO_TYPE_CONFIG = 1;
    private static STEP_GITHUB_CONFIG = 2;
    private static STEP_GIT_CONFIG = 3;
    private static STEP_CREATION = 4;
    private static STEP_INITIALIZATION = 5;

    public repositoryType: RepositoryType;
    public creationRequest: RepositoryCreationRequestDto;

    constructor(private formBuilder: FormBuilder) {
        this.form = this.formBuilder.group({
            stepsForm: this.formBuilder.array([
                this.formBuilder.group({type: this.formBuilder.control('', [Validators.required])}), // step type

                this.formBuilder.group({}), // step config no type selected

                this.formBuilder.group({
                    'username': this.formBuilder.control('', [Validators.required]),
                    'repository': this.formBuilder.control('', [Validators.required]),
                    'accessKey': this.formBuilder.control('', [])
                }), // step repo config github

                this.formBuilder.group({
                    'location': this.formBuilder.control('', [Validators.required]),
                    'name': this.formBuilder.control('', [Validators.required])
                }), // step repo config git

                this.formBuilder.group({}), // step repo creation

                this.formBuilder.group({}) // step repo initialization
            ])
        });
    }

    public get stepTypeEditable(): boolean {
        return !this.repositoryType;
    }

    public get stepTypeForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(RepositoryAddWizardComponent.STEP_TYPE);
    }

    public get stepConfigForm(): FormGroup {
        let index;
        if (!this.repositoryType) {
            index = RepositoryAddWizardComponent.STEP_NO_TYPE_CONFIG;
        } else if (this.repositoryType === RepositoryType.GITHUB) {
            index = RepositoryAddWizardComponent.STEP_GITHUB_CONFIG;
        } else if (this.repositoryType === RepositoryType.GIT) {
            index = RepositoryAddWizardComponent.STEP_GIT_CONFIG;
        }

        return <FormGroup>this.stepsForm.at(index);
    }

    public get stepConfigEditable(): boolean {
        return !this.stepCreationForm.touched || !this.stepConfigForm.valid;
    }

    public get stepCreationForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(RepositoryAddWizardComponent.STEP_CREATION);
    }

    public get stepCreationEditable(): boolean {
        return !this.stepCreationForm.touched || !this.stepCreationForm.valid;
    }

    public get stepInitializationForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(RepositoryAddWizardComponent.STEP_INITIALIZATION);
    }

    public onRepositoryType(repositoryType: RepositoryType) {
        this.repositoryType = repositoryType;
        this.wizard.nextStep();
    }

    public onStepChange(stepChangeEvent: StepChangeEvent) {
        if(stepChangeEvent.nextStepIndex == 2){
            this.creationRequest = this.createRequest();
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
