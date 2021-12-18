import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { RepositoryType } from '@i18n-core-translation';

@Component({
  selector: 'app-repository-add-wizard-step-info',
  templateUrl: './repository-add-wizard-step-info.component.html',
  styleUrls: ['./repository-add-wizard-step-info.component.css'],
})
export class RepositoryAddWizardStepInfoComponent {
  RepositoryType = RepositoryType;

  @Input() form: FormGroup;
  @Input() repositoryType: RepositoryType;

  constructor() {}
}
