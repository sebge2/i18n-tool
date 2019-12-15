import {Component, Input} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {RepositoryType} from "../../../../../../translations/model/repository/repository-type.model";

@Component({
    selector: 'app-repository-add-wizard-step-info',
    templateUrl: './repository-add-wizard-step-info.component.html',
    styleUrls: ['./repository-add-wizard-step-info.component.css']
})
export class RepositoryAddWizardStepInfoComponent {

    public RepositoryType = RepositoryType;

    @Input() public form: FormGroup;
    @Input() public repositoryType: RepositoryType;

    constructor() {
    }
}
