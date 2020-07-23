import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EventService} from "../../core/event/service/event.service";
import {Observable, Subject} from "rxjs";
import {Repository} from "../model/repository.model";
import {RepositoryService as ApiRepositoryService} from "../../api";
import {NotificationService} from "../../core/notification/service/notification.service";
import {synchronizedCollection} from "../../core/shared/utils/synchronized-observable-utils";
import {Events} from "../../core/event/model/events.model";
import {catchError} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class RepositoryService implements OnDestroy {

    private readonly _repositories$: Observable<Repository[]>;
    private readonly _destroyed$ = new Subject();

    constructor(private apiRepositoryService: ApiRepositoryService,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this._repositories$ = synchronizedCollection(
            apiRepositoryService.findAll(),
            this.eventService.subscribeDto(Events.ADDED_REPOSITORY),
            this.eventService.subscribeDto(Events.UPDATED_REPOSITORY),
            this.eventService.subscribeDto(Events.DELETED_REPOSITORY),
            dto => Repository.fromDto(dto),
            ((first, second) => first.id === second.id)
        )
            .pipe(catchError((reason) => {
                console.error("Error while retrieving repositories.", reason);
                this.notificationService.displayErrorMessage("Error while retrieving repositories.");
                return [];
            }));
    }

    ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    getRepositories(): Observable<Repository[]> {
        return this._repositories$;
    }

    // initialize(): Promise<any> {
    //     return this.httpClient
    //         .put(
    //             '/api/repository',
    //             null,
    //             {
    //                 params: {
    //                     do: 'INITIALIZE'
    //                 }
    //             }
    //         )
    //         .toPromise()
    //         .catch(reason => {
    //             console.error("Error while initializing the repository.", reason);
    //             this.notificationService.displayErrorMessage("Error while initializing the repository.");
    //         });
    // }
}
