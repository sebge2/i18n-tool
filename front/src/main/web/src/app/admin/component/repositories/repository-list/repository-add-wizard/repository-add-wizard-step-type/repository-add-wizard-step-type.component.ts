import {Component, Input} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {RepositoryType} from "../../../../../../translations/model/repository-type.model";

@Component({
    selector: 'app-repository-add-wizard-step-type',
    templateUrl: './repository-add-wizard-step-type.component.html',
    styleUrls: ['./repository-add-wizard-step-type.component.scss']
})
export class RepositoryAddWizardStepTypeComponent {

    @Input() public form: FormGroup;

    public availableTypes = [RepositoryType.GITHUB, RepositoryType.GIT];

    constructor() {
    }

    public onSelect(type: RepositoryType) {
        this.form.controls['type'].setValue(type);
    }
}
