import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Repository } from '@i18n-core-translation';
import { MatDialog } from '@angular/material/dialog';
import { RepositoryAddWizardComponent } from './repository-add-wizard/repository-add-wizard.component';

@Component({
  selector: 'app-repository-list',
  templateUrl: './repository-list.component.html',
  styleUrls: ['./repository-list.component.css'],
})
export class RepositoryListComponent {
  @Input() repositories: Repository[] = [];
  @Output() open = new EventEmitter<Repository>();

  constructor(public dialog: MatDialog) {}

  onAdd() {
    this.dialog.open(RepositoryAddWizardComponent);
  }

  openOpen(repository: Repository) {
    this.open.emit(repository);
  }
}
