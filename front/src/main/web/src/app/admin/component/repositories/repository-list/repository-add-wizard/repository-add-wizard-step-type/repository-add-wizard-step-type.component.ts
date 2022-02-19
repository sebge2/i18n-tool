import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {RepositoryIconPipe, RepositoryType} from '@i18n-core-translation';
import {CardSelectorItem, TranslationKey} from "@i18n-core-shared";

@Component({
    selector: 'app-repository-add-wizard-step-type',
    templateUrl: './repository-add-wizard-step-type.component.html',
})
export class RepositoryAddWizardStepTypeComponent {
    @Input() form: FormGroup;

    availableTypes: CardSelectorItem[] = [];

    constructor() {
        const pipe = new RepositoryIconPipe();

        this.availableTypes = [
            {
                id: RepositoryType.GITHUB,
                icon: pipe.transform(RepositoryType.GITHUB),
                label: new TranslationKey('ADMIN.REPOSITORIES.ADD_WIZARD.STEP_TYPE.GITHUB')
            },
            {
                id: RepositoryType.GIT,
                icon: pipe.transform(RepositoryType.GIT),
                label: new TranslationKey('ADMIN.REPOSITORIES.ADD_WIZARD.STEP_TYPE.GIT')
            }
        ]
    }

    onSelect(type: RepositoryType) {
        this.form.controls['type'].setValue(type);
    }
}
