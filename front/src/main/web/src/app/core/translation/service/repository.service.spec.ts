import { getTestBed, TestBed } from '@angular/core/testing';

import { RepositoryService } from './repository.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CoreEventModule } from '@i18n-core-event';
import { Repository } from '../model/repository/repository.model';
import { RepositoryStatus } from '../model/repository/repository-status.model';
import { EventService } from '@i18n-core-event';
import { Observable, Subject } from 'rxjs';
import { Events } from '@i18n-core-event';
import { NotificationService } from '@i18n-core-notification';
import { GitRepository } from '../model/repository/git-repository.model';
import { GitRepositoryDto, RepositoryDto } from '../../../api';

describe('RepositoryService', () => {
  let injector: TestBed;
  let service: RepositoryService;
  let httpMock: HttpTestingController;
  let eventService: MockEventService;
  let notificationService: NotificationService;

  class MockEventService {
    readonly subject: Subject<Repository> = new Subject();

    public subscribe(eventType: string, type: any): Observable<Repository> {
      expect(eventType).toEqual(Events.UPDATED_REPOSITORY);
      expect(type).toEqual(Repository);

      return this.subject;
    }
  }

  beforeEach(() => {
    eventService = new MockEventService();
    notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CoreEventModule],
      providers: [
        RepositoryService,
        { provide: EventService, useValue: eventService },
        { provide: NotificationService, useValue: notificationService },
      ],
    });

    injector = getTestBed();
    service = injector.get(RepositoryService);
    httpMock = injector.get(HttpTestingController);
  });

  xit('should get repository', async () => {
    const firstExpected = GitRepository.fromDto(<GitRepositoryDto>{ status: RepositoryStatus.NOT_INITIALIZED });
    const secondExpected = GitRepository.fromDto(<GitRepositoryDto>{ status: RepositoryStatus.INITIALIZED });

    // const promise = service.getRepository()
    //     .pipe(take(2), toArray())
    //     .toPromise()
    //     .then((actual: Repository[]) => {
    //         expect(actual).toEqual([firstExpected, secondExpected]);
    //     });
    //
    // httpMock.expectOne('/api/repository').flush(secondExpected);
    // httpMock.verify();
    //
    // return promise;
  });

  xit('should get latest repository', async () => {
    const firstExpected = GitRepository.fromDto(<GitRepositoryDto>{ status: RepositoryStatus.NOT_INITIALIZED });
    const secondExpected = GitRepository.fromDto(<GitRepositoryDto>{ status: RepositoryStatus.INITIALIZED });

    // const firstPromise = service.getRepository()
    //     .pipe(take(2), toArray())
    //     .toPromise()
    //     .then(actual => {
    //         expect(actual).toEqual([firstExpected, secondExpected]);
    //         return actual;
    //     });
    //
    // httpMock.expectOne('/api/repository').flush(secondExpected);
    // httpMock.verify();
    //
    // await firstPromise;
    //
    // const secondPromise = service.getRepository()
    //     .pipe(take(1), toArray())
    //     .toPromise()
    //     .then(actual => {
    //         expect(actual).toEqual([secondExpected]);
    //         return actual;
    //     });
    //
    // await secondPromise;
  });

  xit('should get latest updated repository', async () => {
    const firstExpected = GitRepository.fromDto(<GitRepositoryDto>{
      status: RepositoryDto.StatusDtoEnum.NOTINITIALIZED,
    });
    const secondExpected = GitRepository.fromDto(<GitRepositoryDto>{
      status: RepositoryDto.StatusDtoEnum.INITIALIZATIONERROR,
    });
    const thirdExpected = GitRepository.fromDto(<GitRepositoryDto>{ status: RepositoryDto.StatusDtoEnum.INITIALIZED });

    // const promise = service.getRepository()
    //     .pipe(take(3), toArray())
    //     .toPromise()
    //     .then(actual => {
    //         expect(actual).toEqual([firstExpected, secondExpected, thirdExpected]);
    //         return actual;
    //     });

    httpMock.expectOne('/api/repository').flush(secondExpected);
    httpMock.verify();

    // service.getRepository()
    //     .pipe(take(2))
    //     .toPromise()
    //     .then(actual => {
    //         eventService.subject.next(thirdExpected);
    //     });

    // return promise;
  });

  xit('should initialize repository', () => {
    // service.initialize();
    //
    // expect(httpMock.expectOne('/api/repository').request.method).toBe('GET');
  });
});
