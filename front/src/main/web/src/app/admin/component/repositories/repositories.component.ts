import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Repository} from "../../../translations/model/repository/repository.model";
import * as _ from "lodash";
import {TabsComponent} from "../../../core/shared/component/tabs/tabs.component";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {RepositoryService} from "../../../translations/service/repository.service";

@Component({
    selector: 'app-repositories',
    templateUrl: './repositories.component.html',
    styleUrls: ['./repositories.component.css']
})
export class RepositoriesComponent implements OnInit, OnDestroy {

    public openedRepositories: Repository[] = [];
    public repositories: Repository[] = [];

    @ViewChild('tabs', {static: false}) private tabs: TabsComponent;
    private _destroyed$ = new Subject<void>();
    private _initialTab: string;

    constructor(private _repositoryService: RepositoryService) {
    }

    public ngOnInit() {
        this._repositoryService
            .getRepositories()
            .pipe(takeUntil(this._destroyed$))
            .subscribe((repositories) => this.updateRepositories(repositories));
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public onOpen(repository: Repository) {
        const index = _.findIndex(this.openedRepositories, rep => rep.id == repository.id);

        if (index < 0) {
            this.openedRepositories.push(repository);
            this.tabs.selectTab(this.openedRepositories.length)
        } else {
            this.tabs.selectTab(index + 1);
        }
    }

    public onClose(repository: Repository) {
        _.remove(this.openedRepositories, rep => rep.id == repository.id);
    }

    public onInitialTab(initialTab: string) {
        this._initialTab = initialTab;
    }

    public updateRepositories(repositories: Repository[]) {
        this.repositories = repositories;

        if(this._initialTab){
            const index = _.findIndex(this.repositories, repo => _.isEqual(repo.id, this._initialTab));

            if(index >= 0){
                this.onOpen(this.repositories[index]);
            }
        }

        this._initialTab = null;
    }
}
