import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EventService} from "../../core/event/service/event.service";
import {Workspace} from "../model/workspace.model";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {Events} from 'src/app/core/event/model/model.events.model';
import {takeUntil} from "rxjs/operators";
import {NotificationService} from "../../core/notification/service/notification.service";

@Injectable({
    providedIn: 'root'
})
export class WorkspaceService implements OnDestroy {

    private _workspaces: BehaviorSubject<Workspace[]> = new BehaviorSubject<Workspace[]>([]);
    private destroy$ = new Subject();

    constructor(private httpClient: HttpClient,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this.httpClient.get<Workspace[]>('/api/workspace').toPromise()
            .then(workspaces => this._workspaces.next(workspaces.map(workspace => new Workspace(workspace)).sort(workspaceSorter)))
            .catch(reason => this.notificationService.displayErrorMessage("Error while retrieving workspaces.", reason));

        this.eventService.subscribe(Events.UPDATED_WORKSPACE, Workspace)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (workspace: Workspace) => {
                    const workspaces = this._workspaces.getValue().slice();

                    const index = workspaces.findIndex(current => workspace.id === current.id);
                    if (index >= 0) {
                        workspaces[index] = workspace;
                    } else {
                        workspaces.push(workspace);
                    }

                    this._workspaces.next(workspaces.sort(workspaceSorter));
                }
            );

        this.eventService.subscribe(Events.DELETED_WORKSPACE, Workspace)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (workspace: Workspace) => {
                    const workspaces = this._workspaces.getValue().slice();

                    const index = workspaces.findIndex(current => workspace.id === current.id);
                    if (index >= 0) {
                        workspaces.splice(index, 1);
                    }

                    this._workspaces.next(workspaces.sort(workspaceSorter));
                }
            );
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    getWorkspaces(): Observable<Workspace[]> {
        return this._workspaces;
    }

    initialize(workspace: Workspace): Promise<any> {
        return this.httpClient
            .put(
                '/api/workspace/' + workspace.id,
                null,
                {
                    params: {
                        do: 'INITIALIZE'
                    }
                }
            )
            .toPromise()
            .catch(reason => this.notificationService.displayErrorMessage("Error while initializing workspace.", reason));
    }

    find(): Promise<any> {
        return this.httpClient
            .put(
                '/api/workspace/',
                null,
                {
                    params: {
                        do: 'FIND'
                    }
                }
            )
            .toPromise()
            .catch(reason => this.notificationService.displayErrorMessage("Error while finding workspaces. ", reason));
    }

    startReview(workspace: Workspace, comment: string): Promise<any> {
        return this.httpClient
            .put(
                '/api/workspace/' + workspace.id,
                null,
                {
                    params: {
                        do: 'START_REVIEW',
                        message: comment
                    }
                }
            )
            .toPromise()
            .catch(reason => this.notificationService.displayErrorMessage("Error while starting review.", reason));
    }

    delete(workspace: Workspace): Promise<any> {
        return this.httpClient
            .delete('/api/workspace/' + workspace.id)
            .toPromise()
            .catch(reason => this.notificationService.displayErrorMessage("Error while deleting.", reason));
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
