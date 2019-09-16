import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EventService} from "../../core/event/service/event.service";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {Repository} from "../model/repository.model";
import {Events} from "../../core/event/model.events.model";
import {RepositoryStatus} from "../model/repository-status.model";
import {takeUntil} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class RepositoryService implements OnDestroy {

    private _repository: BehaviorSubject<Repository> = new BehaviorSubject<Repository>(new Repository(<Repository>{status: RepositoryStatus.NOT_INITIALIZED}));
    private destroy$ = new Subject();

    constructor(private httpClient: HttpClient,
                private eventService: EventService) {
        this.httpClient.get<Repository>('/api/repository')
            .pipe(takeUntil(this.destroy$))
            .toPromise()
            .then(repository => this._repository.next(new Repository(repository)))
            .catch(reason => console.error("Error while retrieving the repository.", reason));

        this.eventService.subscribe(Events.UPDATED_REPOSITORY, Repository)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (repository: Repository) => {
                    this._repository.next(repository);
                }
            );
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    getRepository(): Observable<Repository> {
        return this._repository;
    }
}
