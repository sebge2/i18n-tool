import {Component, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from "@angular/forms";
import {RepositoryType} from "../../../../../translations/model/repository-type.model";
import * as _ from "lodash";
import {WizardComponent} from "../../../../../core/shared/component/wizard/wizard.component";

@Component({
    selector: 'app-repository-add-wizard',
    templateUrl: './repository-add-wizard.component.html',
    styleUrls: ['./repository-add-wizard.component.css']
})
export class RepositoryAddWizardComponent {

    @ViewChild('wizard', {static: true}) wizard: WizardComponent;

    public form: FormGroup;

    private static STEP_TYPE = 0;
    private static STEP_CONFIG = 1;
    private static STEP_CREATION = 2;
    private static STEP_INITIALIZATION = 3;

    constructor(private formBuilder: FormBuilder) {
        this.form = this.formBuilder.group({
            stepsForm: this.formBuilder.array([
                this.formBuilder.group({}), // step type
                this.formBuilder.group({}), // step repo config
                this.formBuilder.group({}), // step repo creation
                this.formBuilder.group({}) // step repo initialization
            ])
        });
    }

    public get stepTypeEditable(): boolean {
        return this.wizard.selectedIndex == RepositoryAddWizardComponent.STEP_TYPE;
    }

    public get stepTypeForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(RepositoryAddWizardComponent.STEP_TYPE);
    }

    public get repositoryType(): RepositoryType {
        return _.get(this.stepTypeForm.controls['type'], 'value');
    }

    public get stepConfigForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(RepositoryAddWizardComponent.STEP_CONFIG);
    }

    public get stepCreationForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(RepositoryAddWizardComponent.STEP_CREATION);
    }

    public get stepInitializationForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(RepositoryAddWizardComponent.STEP_INITIALIZATION);
    }

    public onNextStep() {
        this.wizard.nextStep();
    }

    private get stepsForm(): FormArray | null {
        return <FormArray>this.form.controls['stepsForm'];
    }
}
