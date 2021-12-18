import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { RepositoryType } from '@i18n-core-translation';

@Component({
  selector: 'app-repository-add-wizard-step-type',
  templateUrl: './repository-add-wizard-step-type.component.html',
  styleUrls: ['./repository-add-wizard-step-type.component.scss'],
})
export class RepositoryAddWizardStepTypeComponent {
  @Input() form: FormGroup;

  availableTypes = [RepositoryType.GITHUB, RepositoryType.GIT];

  constructor() {}

  onSelect(type: RepositoryType) {
    this.form.controls['type'].setValue(type);
  }
}
