import {Component} from '@angular/core';
import {Repository} from "../../../translations/model/repository.model";
import * as _ from "lodash";

@Component({
    selector: 'app-repositories',
    templateUrl: './repositories.component.html',
    styleUrls: ['./repositories.component.css']
})
export class RepositoriesComponent {

    public openedRepositories: Repository[] = [];

    constructor() {
    }

    public onOpen(repository: Repository) {
        if (!_.find(this.openedRepositories, rep => rep.id == repository.id)) {
            this.openedRepositories.push(repository);
        }
    }

    public onClose(repository: Repository) {
        _.remove(this.openedRepositories, rep => rep.id == repository.id);
    }
}
