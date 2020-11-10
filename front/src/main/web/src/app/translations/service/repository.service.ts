import {Injectable} from '@angular/core';
import {EventService} from "../../core/event/service/event.service";
import {Observable} from "rxjs";
import {Repository} from "../model/repository/repository.model";
import {
    BodyDto,
    GitHubRepositoryCreationRequestDto,
    GitHubRepositoryDto,
    GitRepositoryCreationRequestDto,
    GitRepositoryDto,
    RepositoryCreationRequestDto,
    RepositoryDto,
    RepositoryPatchRequestDto,
    RepositoryService as ApiRepositoryService
} from "../../api";
import {NotificationService} from "../../core/notification/service/notification.service";
import {Events} from "../../core/event/model/events.model";
import {catchError, distinctUntilChanged, filter, map, tap} from "rxjs/operators";
import {GitRepository} from "../model/repository/git-repository.model";
import {GitHubRepository} from "../model/repository/github-repository.model";
import * as _ from "lodash";
import {SynchronizedCollection} from "../../core/shared/utils/synchronized-collection";

@Injectable({
    providedIn: 'root'
})
export class RepositoryService {

    private readonly _synchronizedRepositories: SynchronizedCollection<RepositoryDto, Repository>;
    private readonly _repositories$: Observable<Repository[]>;

    constructor(private apiRepositoryService: ApiRepositoryService,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this._synchronizedRepositories = new SynchronizedCollection<RepositoryDto, Repository>(
            () => apiRepositoryService.findAll(),
            this.eventService.subscribeDto(Events.ADDED_REPOSITORY),
            this.eventService.subscribeDto(Events.UPDATED_REPOSITORY),
            this.eventService.subscribeDto(Events.DELETED_REPOSITORY),
            this.eventService.reconnected(),
            dto => RepositoryService.fromDto(dto),
            ((first, second) => first.id === second.id)
        );

        this._repositories$ = this._synchronizedRepositories
            .collection
            .pipe(catchError((reason) => {
                console.error('Error while retrieving repositories.', reason);
                this.notificationService.displayErrorMessage('ADMIN.REPOSITORIES.ERROR.GET_ALL');
                return [];
            }));
    }

    public getRepositories(): Observable<Repository[]> {
        return this._repositories$;
    }

    public getRepository(repositoryId: string): Observable<Repository | undefined> {
        return this.getRepositories()
            .pipe(
                map(repositories => _.find(repositories, repository => _.isEqual(repository.id, repositoryId))),
                distinctUntilChanged()
            );
    }

    public createRepository(dto: RepositoryCreationRequestDto): Observable<Repository> {
        return this.apiRepositoryService
            .create(<(GitHubRepositoryCreationRequestDto | GitRepositoryCreationRequestDto)>dto)
            .pipe(
                map(dto => RepositoryService.fromDto(dto)),
                tap(repository => this._synchronizedRepositories.add(repository))
            );
    }

    public initializeRepository(id: string): Observable<Repository> {
        return this.apiRepositoryService
            .initialize(id, 'INITIALIZE')
            .pipe(
                map(dto => RepositoryService.fromDto(dto)),
                tap(repository => this._synchronizedRepositories.update(repository))
            );
    }

    public updateRepository(id: string, patch: RepositoryPatchRequestDto): Observable<Repository> {
        return this.apiRepositoryService
            .update(<BodyDto>patch, id)
            .pipe(
                map(dto => RepositoryService.fromDto(dto)),
                tap(repository => this._synchronizedRepositories.update(repository))
            );
    }

    public deleteRepository(repository: Repository): Observable<any> {
        return this.apiRepositoryService
            ._delete(repository.id)
            .pipe(tap(() => this._synchronizedRepositories.delete(repository)));
    }

    private static fromDto(dto: RepositoryDto): Repository {
        switch (dto.type) {
            case "GIT":
                return GitRepository.fromDto(<GitRepositoryDto>dto);
            case "GITHUB":
                return GitHubRepository.fromDto(<GitHubRepositoryDto>dto);
            default:
                throw new Error(`Unsupported type ${dto.type}.`)
        }
    }
}
