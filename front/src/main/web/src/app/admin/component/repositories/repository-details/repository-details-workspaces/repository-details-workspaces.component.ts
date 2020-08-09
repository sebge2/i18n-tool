import {Component, Input, OnInit} from '@angular/core';
import {Repository} from "../../../../../translations/model/repository/repository.model";
import {RepositoryStatus} from "../../../../../translations/model/repository/repository-status.model";
import {RepositoryService} from "../../../../../translations/service/repository.service";
import {NotificationService} from "../../../../../core/notification/service/notification.service";

@Component({
    selector: 'app-repository-details-workspaces',
    templateUrl: './repository-details-workspaces.component.html',
    styleUrls: ['./repository-details-workspaces.component.css']
})
export class RepositoryDetailsWorkspacesComponent implements OnInit {

    @Input() public repository: Repository;

    public RepositoryStatus = RepositoryStatus;
    public initInProgress = false;

    constructor(private _repositoryService: RepositoryService,
                private _notificationService: NotificationService) {
    }

    ngOnInit(): void {
    }

    public onInitialize() {
        this.initInProgress = true;

        this._repositoryService
            .initializeRepository(this.repository.id)
            .toPromise()
            .then(repository => this.repository = repository)
            .catch(error => {
                console.error('Error while initializing repository.', error);
                this._notificationService.displayErrorMessage('ADMIN.REPOSITORIES.ERROR.INITIALIZE', error);
            })
            .finally(() => this.initInProgress = false);
    }
}
