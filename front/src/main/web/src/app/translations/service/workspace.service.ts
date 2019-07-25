import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EventService} from "../../core/event/service/event.service";
import {Workspace} from "../model/workspace.model";
import {BehaviorSubject, Observable} from "rxjs";
import {UserSession} from "../../core/auth/model/user-session.model";

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

        // this._connectedUserSessionObservable = this.eventService.subscribe("updated-workspace", Workspace)
        //     .subscribe(
        //         (userSession: UserSession) => {
        //             let userSessions = this._userSessions.getValue().slice();
        //             userSessions.push(userSession);
        //
        //             this._userSessions.next(userSessions);
        //         }
        //     );
        //
        // this._disconnectedUserSessionObservable = this.eventService.subscribe("deleted-workspace", Workspace)
        //     .subscribe(
        //         (userSession: UserSession) => {
        //             let userSessions = this._userSessions.getValue().slice();
        //             userSessions.splice(userSessions.indexOf(userSession), 1);
        //
        //             this._userSessions.next(userSessions);
        //         }
        //     );
    }

    ngOnDestroy(): void {
    }

    getWorkspaces(): BehaviorSubject<Workspace[]> {
        return this._workspaces;
    }
}
