import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EventService} from "../../core/event/service/event.service";
import {Workspace} from "../model/workspace.model";
import {BehaviorSubject, Subscription} from "rxjs";
import {Events} from 'src/app/core/event/model.events.model';

@Injectable({
    providedIn: 'root'
})
export class WorkspaceService implements OnDestroy {

    private _updatedWorkspaceObservable: Subscription;
    private _deletedWorkspaceObservable: Subscription;

    private _workspaces: BehaviorSubject<Workspace[]> = new BehaviorSubject<Workspace[]>([]);

    constructor(private httpClient: HttpClient,
                private eventService: EventService) {
        this.httpClient.get<Workspace[]>('/api/workspace').toPromise()
            .then(workspaces => this._workspaces.next(workspaces))
            .catch(reason => console.error("Error while retrieving workspaces.", reason));

        this._updatedWorkspaceObservable = this.eventService.subscribe(Events.UPDATED_WORKSPACE, Workspace)
            .subscribe(
                (workspace: Workspace) => {
                    this._workspaces.next(this._workspaces.getValue().map(w => workspace.id === w.id ? workspace : w));
                }
            );

        this._deletedWorkspaceObservable = this.eventService.subscribe(Events.DELETED_WORKSPACE, Workspace)
            .subscribe(
                (workspace: Workspace) => {
                    let workspaces = this._workspaces.getValue().slice();
                    workspaces.splice(workspaces.findIndex(current => workspace.id === current.id), 1);

                    this._workspaces.next(workspaces);
                }
            );
    }

    ngOnDestroy(): void {
        this._updatedWorkspaceObservable.unsubscribe();
        this._deletedWorkspaceObservable.unsubscribe();
    }

    getWorkspaces(): BehaviorSubject<Workspace[]> {
        return this._workspaces;
    }
}
