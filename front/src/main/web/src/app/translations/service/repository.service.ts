import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EventService} from "../../core/event/service/event.service";
import {BehaviorSubject, Subscription} from "rxjs";
import {Repository} from "../model/repository.model";
import {Events} from "../../core/event/model.events.model";

@Injectable({
    providedIn: 'root'
})
export class RepositoryService implements OnDestroy {

    private _repositorySubscription: Subscription;

    private _repository: BehaviorSubject<Repository> = new BehaviorSubject<Repository>(new Repository(<Repository>{initialized: false}));

    constructor(private httpClient: HttpClient,
                private eventService: EventService) {
        this.httpClient.get<Repository>('/api/repository').toPromise()
            .then(repository => this._repository.next(new Repository(repository)))
            .catch(reason => console.error("Error while retrieving the repository.", reason));

        this._repositorySubscription = this.eventService.subscribe(Events.UPDATED_REPOSITORY, Repository)
            .subscribe(
                (repository: Repository) => {
                    this._repository.next(repository);
                }
            );
    }

    ngOnDestroy(): void {
        this._repositorySubscription.unsubscribe();
    }

    getRepository(): BehaviorSubject<Repository> {
        return this._repository;
    }
}
