import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EventService} from "../../core/event/service/event.service";
import {Workspace} from "../model/workspace.model";
import {BehaviorSubject, Subscription} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class WorkspaceService implements OnDestroy {

    private _connectedUserSessionObservable: Subscription;
    private _disconnectedUserSessionObservable: Subscription;

    private _workspaces: BehaviorSubject<Workspace[]> = new BehaviorSubject<Workspace[]>([]);

    constructor(private httpClient: HttpClient,
                private eventService: EventService) {
        this.httpClient.get<Workspace[]>('/api/workspace').toPromise()
            .then(workspaces => this._workspaces.next(workspaces))
            .catch(reason => console.error("Error while retrieving workspaces.", reason));

        this._connectedUserSessionObservable = this.eventService.subscribe("updated-workspace", Workspace)
            .subscribe(
                (workspace: Workspace) => {
                    this._workspaces.next(this._workspaces.getValue().map(w => workspace.id === w.id ? workspace : w));
                }
            );

        this._disconnectedUserSessionObservable = this.eventService.subscribe("deleted-workspace", Workspace)
            .subscribe(
                (workspace: Workspace) => {
                    let workspaces = this._workspaces.getValue().slice();
                    workspaces.splice(workspaces.findIndex(current => workspace.id === current.id), 1);

                    this._workspaces.next(workspaces);
                }
            );
    }

    ngOnDestroy(): void {
        this._connectedUserSessionObservable.unsubscribe();
        this._disconnectedUserSessionObservable.unsubscribe();
    }

    getWorkspaces(): BehaviorSubject<Workspace[]> {
        return this._workspaces;
    }
}
