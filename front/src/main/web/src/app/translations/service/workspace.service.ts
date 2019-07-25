import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EventService} from "../../core/event/service/event.service";
import {Workspace} from "../model/workspace.model";
import {BehaviorSubject} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class WorkspaceService implements OnDestroy {

    private _workspaces: BehaviorSubject<Workspace[]> = new BehaviorSubject<Workspace[]>([]);

    constructor(private httpClient: HttpClient,
                private eventService: EventService) {
        this.httpClient.get<Workspace[]>('/api/workspace').toPromise()
            .then(workspaces => this._workspaces.next(workspaces))
            .catch(reason => console.error("Error while retrieving workspaces.", reason));

        // TODO events
    }

    ngOnDestroy(): void {
    }

    getWorkspaces(): BehaviorSubject<Workspace[]> {
        return this._workspaces;
    }
}
