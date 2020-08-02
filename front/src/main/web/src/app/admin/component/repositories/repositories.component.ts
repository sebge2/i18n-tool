import {Component, ViewChild} from '@angular/core';
import {Repository} from "../../../translations/model/repository.model";
import * as _ from "lodash";
import {TabsComponent} from "../../../core/shared/component/tabs/tabs.component";

@Component({
    selector: 'app-repositories',
    templateUrl: './repositories.component.html',
    styleUrls: ['./repositories.component.css']
})
export class RepositoriesComponent {

    public openedRepositories: Repository[] = [];

    @ViewChild('tabs', {static: false}) private tabs: TabsComponent;

    constructor() {
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
}
