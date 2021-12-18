import { Injectable } from '@angular/core';
import { EventService } from '@i18n-core-event';
import { Observable } from 'rxjs';
import { Repository } from '../model/repository/repository.model';
import {
  GitHubRepositoryCreationRequestDto,
  GitHubRepositoryDto,
  GitRepositoryCreationRequestDto,
  GitRepositoryDto,
  RepositoryCreationRequestDto,
  RepositoryDto,
  RepositoryPatchRequestDto,
  RepositoryService as ApiRepositoryService,
} from '../../../api';
import { NotificationService } from '@i18n-core-notification';
import { Events } from '@i18n-core-event';
import { catchError, distinctUntilChanged, map, tap } from 'rxjs/operators';
import { GitRepository } from '../model/repository/git-repository.model';
import { GitHubRepository } from '../model/repository/github-repository.model';
import * as _ from 'lodash';
import { SynchronizedCollection } from '@i18n-core-shared';

@Injectable({
  providedIn: 'root',
})
export class RepositoryService {
  private readonly _synchronizedRepositories: SynchronizedCollection<RepositoryDto, Repository>;
  private readonly _repositories$: Observable<Repository[]>;

  constructor(
    private _apiRepositoryService: ApiRepositoryService,
    private _eventService: EventService,
    private _notificationService: NotificationService
  ) {
    this._synchronizedRepositories = new SynchronizedCollection<RepositoryDto, Repository>(
      () => _apiRepositoryService.findAll3(),
      this._eventService.subscribeDto(Events.ADDED_REPOSITORY),
      this._eventService.subscribeDto(Events.UPDATED_REPOSITORY),
      this._eventService.subscribeDto(Events.DELETED_REPOSITORY),
      this._eventService.reconnected(),
      (dto) => RepositoryService._fromDto(dto),
      (first, second) => first.id === second.id
    );

    this._repositories$ = this._synchronizedRepositories.collection.pipe(
      catchError((reason) => {
        console.error('Error while retrieving repositories.', reason);
        this._notificationService.displayErrorMessage('ADMIN.REPOSITORIES.ERROR.GET_ALL');
        return [];
      })
    );
  }

  getRepositories(): Observable<Repository[]> {
    return this._repositories$;
  }

  getRepository(repositoryId: string): Observable<Repository | undefined> {
    return this.getRepositories().pipe(
      map((repositories) => _.find(repositories, (repository) => _.isEqual(repository.id, repositoryId))),
      distinctUntilChanged()
    );
  }

  createRepository(dto: RepositoryCreationRequestDto): Observable<Repository> {
    return this._apiRepositoryService
      .create2(<GitHubRepositoryCreationRequestDto | GitRepositoryCreationRequestDto>dto)
      .pipe(
        map((dto) => RepositoryService._fromDto(dto)),
        tap((repository) => this._synchronizedRepositories.add(repository))
      );
  }

  initializeRepository(id: string): Observable<Repository> {
    return this._apiRepositoryService.initialize(id, 'INITIALIZE').pipe(
      map((dto) => RepositoryService._fromDto(dto)),
      tap((repository) => this._synchronizedRepositories.update(repository))
    );
  }

  updateRepository(id: string, patch: RepositoryPatchRequestDto): Observable<Repository> {
    return this._apiRepositoryService.update1(patch, id).pipe(
      map((dto) => RepositoryService._fromDto(dto)),
      tap((repository) => this._synchronizedRepositories.update(repository))
    );
  }

  deleteRepository(repository: Repository): Observable<any> {
    return this._apiRepositoryService
      .delete3(repository.id)
      .pipe(tap(() => this._synchronizedRepositories.delete(repository)));
  }

  private static _fromDto(dto: RepositoryDto): Repository {
    switch (dto.type) {
      case 'GIT':
        return GitRepository.fromDto(<GitRepositoryDto>dto);
      case 'GITHUB':
        return GitHubRepository.fromDto(<GitHubRepositoryDto>dto);
      default:
        throw new Error(`Unsupported type ${dto.type}.`);
    }
  }
}
