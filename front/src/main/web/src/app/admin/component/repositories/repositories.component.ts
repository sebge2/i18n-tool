import {Component, OnDestroy, OnInit} from '@angular/core';
import {RepositoryService} from "../../../translations/service/repository.service";
import {Repository} from "../../../translations/model/repository.model";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

@Component({
  selector: 'app-repositories',
  templateUrl: './repositories.component.html',
  styleUrls: ['./repositories.component.css']
})
export class RepositoriesComponent implements OnInit, OnDestroy {

  public repositories: Repository[] = [];

  private _destroyed$ = new Subject<void>();

  constructor(private _repositoryService: RepositoryService) { }

  public ngOnInit() {
    this._repositoryService
        .getRepositories()
        .pipe(takeUntil(this._destroyed$))
        .subscribe(rep => this.repositories = rep);
  }

  public ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  public onSave(locale: any) {

  }

  public onAdd() {

  }
}
