import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {Repository} from "../../../../translations/model/repository.model";
import {Subject} from "rxjs";
import {RepositoryService} from "../../../../translations/service/repository.service";
import {takeUntil} from "rxjs/operators";
import {MatDialog} from "@angular/material/dialog";
import {RepositoryAddWizardComponent} from "./repository-add-wizard/repository-add-wizard.component";

@Component({
    selector: 'app-repository-list',
    templateUrl: './repository-list.component.html',
    styleUrls: ['./repository-list.component.css']
})
export class RepositoryListComponent implements OnInit, OnDestroy {

    @Output() public open = new EventEmitter<Repository>();

    public repositories: Repository[] = [];

    private _destroyed$ = new Subject<void>();

    constructor(private _repositoryService: RepositoryService,
                public dialog: MatDialog) {
    }

    public ngOnInit() {
        this._repositoryService.getRepositories()
            .pipe(takeUntil(this._destroyed$))
            .subscribe((repositories) => {
                this.repositories = repositories;
            });
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public onAdd() {
        this.dialog.open(RepositoryAddWizardComponent, {
            height: '500px',
            width: '800px',
        });
    }

    public openOpen(repository: Repository) {
        this.open.emit(repository);
    }
}
