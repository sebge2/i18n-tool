import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Repository} from "../../../../translations/model/repository/repository.model";
import {MatDialog} from "@angular/material/dialog";
import {RepositoryAddWizardComponent} from "./repository-add-wizard/repository-add-wizard.component";

@Component({
    selector: 'app-repository-list',
    templateUrl: './repository-list.component.html',
    styleUrls: ['./repository-list.component.css']
})
export class RepositoryListComponent {

    @Input() public repositories: Repository[] = [];
    @Output() public open = new EventEmitter<Repository>();

    constructor(public dialog: MatDialog) {
    }

    public onAdd() {
        this.dialog.open(RepositoryAddWizardComponent);
    }

    public openOpen(repository: Repository) {
        this.open.emit(repository);
    }
}
