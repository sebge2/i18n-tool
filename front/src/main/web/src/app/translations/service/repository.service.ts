import {Injectable} from '@angular/core';
import {EventService} from "../../core/event/service/event.service";
import {Observable} from "rxjs";
import {Repository} from "../model/repository/repository.model";
import {
    BodyDto,
    GitHubRepositoryCreationRequestDto, GitHubRepositoryDto,
    GitRepositoryCreationRequestDto, GitRepositoryDto,
    RepositoryCreationRequestDto, RepositoryDto, RepositoryPatchRequestDto,
    RepositoryService as ApiRepositoryService
} from "../../api";
import {NotificationService} from "../../core/notification/service/notification.service";
import {synchronizedCollection} from "../../core/shared/utils/synchronized-observable-utils";
import {Events} from "../../core/event/model/events.model";
import {catchError, map} from "rxjs/operators";
import {GitRepository} from "../model/repository/git-repository.model";
import {GitHubRepository} from "../model/repository/github-repository.model";

@Injectable({
    providedIn: 'root'
})
export class RepositoryService {

    private readonly _repositories$: Observable<Repository[]>;

    constructor(private apiRepositoryService: ApiRepositoryService,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this._repositories$ = synchronizedCollection(
            apiRepositoryService.findAll(),
            this.eventService.subscribeDto(Events.ADDED_REPOSITORY),
            this.eventService.subscribeDto(Events.UPDATED_REPOSITORY),
            this.eventService.subscribeDto(Events.DELETED_REPOSITORY),
            dto => RepositoryService.fromDto(dto),
            ((first, second) => first.id === second.id)
        )
            .pipe(catchError((reason) => {
                console.error("Error while retrieving repositories.", reason);
                this.notificationService.displayErrorMessage("Error while retrieving repositories.");
                return [];
            }));
    }

    public getRepositories(): Observable<Repository[]> {
        return this._repositories$;
    }

    public createRepository(dto: RepositoryCreationRequestDto): Observable<Repository> {
        return this.apiRepositoryService
            .create(<(GitHubRepositoryCreationRequestDto | GitRepositoryCreationRequestDto)>dto)
            .pipe(map(dto => RepositoryService.fromDto(dto)));
    }

    public initializeRepository(id: string): Observable<Repository> {
        return this.apiRepositoryService
            .executeRepositoryAction(id, 'INITIALIZE')
            .pipe(map(dto => RepositoryService.fromDto(dto)));
    }

    public updateRepository(id: string, patch: RepositoryPatchRequestDto): Observable<Repository> {
        return this.apiRepositoryService
            .update(<BodyDto> patch, id)
            .pipe(map(dto => RepositoryService.fromDto(dto)));
    }

    public deleteRepository(id: string): Observable<any> {
        return this.apiRepositoryService
            ._delete(id);
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
