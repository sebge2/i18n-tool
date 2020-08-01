import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RepositoryType} from "../../../../../../translations/model/repository-type.model";
import {WizardComponent} from "../../../../../../core/shared/component/wizard/wizard.component";

@Component({
    selector: 'app-repository-add-wizard-step-type',
    templateUrl: './repository-add-wizard-step-type.component.html',
    styleUrls: ['./repository-add-wizard-step-type.component.scss']
})
export class RepositoryAddWizardStepTypeComponent implements OnInit {

    @Input() public form: FormGroup;
    @Input() public wizard: WizardComponent;

    public availableTypes = [RepositoryType.GITHUB, RepositoryType.GIT];

    constructor(private formBuilder: FormBuilder) {
    }

    public ngOnInit() {
        this.form.setControl('type', this.formBuilder.control('', [Validators.required]));
    }

    public onSelect(type: RepositoryType) {
        this.form.controls['type'].setValue(type);
        this.wizard.nextStep();
    }
}
