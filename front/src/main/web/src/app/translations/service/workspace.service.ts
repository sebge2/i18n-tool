import {Injectable} from '@angular/core';
import {EventService} from "../../core/event/service/event.service";
import {Workspace} from "../model/workspace/workspace.model";
import {Observable} from "rxjs";
import {Events} from 'src/app/core/event/model/events.model';
import {catchError, map} from "rxjs/operators";
import {NotificationService} from "../../core/notification/service/notification.service";
import {synchronizedCollection} from "../../core/shared/utils/synchronized-observable-utils";
import {WorkspaceService as ApiWorkspaceService} from "../../api";
import * as _ from "lodash";

@Injectable({
    providedIn: 'root'
})
export class WorkspaceService {

    private readonly _workspaces$: Observable<Workspace[]>;

    constructor(private apiWorkspaceService: ApiWorkspaceService,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this._workspaces$ = synchronizedCollection(
            apiWorkspaceService.findAll4(),
            this.eventService.subscribeDto(Events.ADDED_WORKSPACE),
            this.eventService.subscribeDto(Events.UPDATED_WORKSPACE),
            this.eventService.subscribeDto(Events.DELETED_WORKSPACE),
            dto => Workspace.fromDto(dto),
            ((first, second) => first.id === second.id)
        )
            .pipe(catchError((reason) => {
                console.error("Error while retrieving workspaces.", reason);
                this.notificationService.displayErrorMessage("Error while retrieving workspaces.");
                return [];
            }));
    }

    public getWorkspaces(): Observable<Workspace[]> {
        return this._workspaces$;
    }

    public getRepositoryWorkspaces(repositoryId: string): Observable<Workspace[]> {
        return this._workspaces$
            .pipe(map(workspaces => workspaces.filter(workspace => _.isEqual(workspace.repositoryId, repositoryId))));
    }
}

export function workspaceSorter(first: Workspace, second: Workspace): number {
    if (first.id === second.id) {
        return 0;
    } else if ("master" === first.branch) {
        return -1;
    } else if ("master" === second.branch) {
        return 1;
    } else {
        return (first.branch < second.branch) ? -1 : 1;
    }
}
