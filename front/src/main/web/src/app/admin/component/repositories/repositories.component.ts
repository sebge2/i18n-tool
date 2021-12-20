import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Repository } from '@i18n-core-translation';
import * as _ from 'lodash';
import { TabsComponent } from '@i18n-core-shared';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RepositoryService } from '@i18n-core-translation';
import { updateOriginalCollection } from '@i18n-core-shared';

@Component({
  selector: 'app-repositories',
  templateUrl: './repositories.component.html',
  styleUrls: ['./repositories.component.css'],
})
export class RepositoriesComponent implements OnInit, OnDestroy {
  openedRepositories: Repository[] = [];
  repositories: Repository[] = [];

  @ViewChild('tabs', { static: false }) private tabs: TabsComponent;

  private readonly _destroyed$ = new Subject<void>();
  private _initialTab: string;

  constructor(private _repositoryService: RepositoryService) {}

  ngOnInit() {
    this._repositoryService
      .getRepositories()
      .pipe(takeUntil(this._destroyed$))
      .subscribe((repositories) => this.updateRepositories(repositories));
  }

  ngOnDestroy(): void {
    this._destroyed$.next(null);
    this._destroyed$.complete();
  }

  identify(index: number, repository: Repository) {
    return repository.id;
  }

  onOpen(repository: Repository) {
    const index = _.findIndex(this.openedRepositories, (rep) => rep.id == repository.id);

    if (index < 0) {
      this.openedRepositories.push(repository);
      this.tabs.selectTab(this.openedRepositories.length);
    } else {
      this.tabs.selectTab(index + 1);
    }
  }

  onClose(repository: Repository) {
    _.remove(this.openedRepositories, (rep) => rep.id == repository.id);
  }

  onInitialTab(initialTab: string) {
    this._initialTab = initialTab;
  }

  updateRepositories(repositories: Repository[]) {
    this.repositories = repositories;

    this.openedRepositories = updateOriginalCollection(this.openedRepositories, repositories, 'id');

    if (this._initialTab) {
      const index = _.findIndex(this.repositories, (repo) => _.isEqual(repo.id, this._initialTab));

      if (index >= 0) {
        this.onOpen(this.repositories[index]);
      }
    }

    this._initialTab = null;
  }
}
