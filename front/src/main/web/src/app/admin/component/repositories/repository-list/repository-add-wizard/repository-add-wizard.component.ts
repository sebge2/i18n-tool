import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatStepper} from "@angular/material/stepper";
import {RepositoryType} from "../../../../../translations/model/repository-type.model";
import * as _ from "lodash";

@Component({
    selector: 'app-repository-add-wizard',
    templateUrl: './repository-add-wizard.component.html',
    styleUrls: ['./repository-add-wizard.component.css']
})
export class RepositoryAddWizardComponent implements OnInit {

    @ViewChild('stepper', {static: true}) stepper: MatStepper;

    public form: FormGroup;

    private static  STEP_TYPE = 0;
    private static  STEP_INFO = 1;

    constructor(private formBuilder: FormBuilder) {
        this.form = this.formBuilder.group({
            stepsForm: this.formBuilder.array([
                this.formBuilder.group({}), // step type
                this.formBuilder.group({}) // step repo info
            ])
        });
    }

    public ngOnInit() {
    }


    public get stepTypeEditable(): boolean {
        return this.stepper.selectedIndex == RepositoryAddWizardComponent.STEP_TYPE;
    }

    public get stepTypeForm(): FormGroup {
        return <FormGroup> this.stepsForm.at(RepositoryAddWizardComponent.STEP_TYPE);
    }

    public get repositoryType(): RepositoryType {
        return _.get(this.stepTypeForm.controls['type'], 'value');
    }

    public get stepInfoForm(): FormGroup {
        return <FormGroup> this.stepsForm.at(RepositoryAddWizardComponent.STEP_INFO);
    }

    private get stepsForm(): FormArray | null {
        return <FormArray> this.form.controls['stepsForm'];
    }
}
