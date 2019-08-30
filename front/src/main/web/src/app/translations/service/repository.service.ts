import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EventService} from "../../core/event/service/event.service";
import {BehaviorSubject} from "rxjs";
import {Repository} from "../model/repository.model";

@Injectable({
    providedIn: 'root'
})
export class RepositoryService {

    private _repository: BehaviorSubject<Repository> = new BehaviorSubject<Repository>(new Repository(<Repository> {initialized: false}));

    constructor(private httpClient: HttpClient,
                private eventService: EventService) {
        // TODO
    }

    getRepository(): BehaviorSubject<Repository> {
        return this._repository;
    }
}
