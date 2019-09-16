import {getTestBed, inject, TestBed} from '@angular/core/testing';

import {RepositoryService} from './repository.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {CoreEventModule} from "../../core/event/core-event.module";
import {Repository} from "../model/repository.model";
import {RepositoryStatus} from "../model/repository-status.model";
import {take, toArray} from "rxjs/operators";
import {EventService} from "../../core/event/service/event.service";
import {Observable, Subject} from "rxjs";
import {Events} from "../../core/event/model.events.model";

describe('RepositoryService', () => {
    let injector: TestBed;
    let service: RepositoryService;
    let httpMock: HttpTestingController;
    let eventService: MockEventService;

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

        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule, CoreEventModule],
            providers: [
                RepositoryService,
                {provide: EventService, useValue: eventService}
            ]
        });

        injector = getTestBed();
        service = injector.get(RepositoryService);
        httpMock = injector.get(HttpTestingController);
    });

    it('should get repository',
        inject(
            [HttpTestingController, RepositoryService],
            async (
                httpMock: HttpTestingController,
                repositoryService: RepositoryService
            ) => {
                const firstExpected = new Repository(<Repository>{status: RepositoryStatus.NOT_INITIALIZED});
                const secondExpected = new Repository(<Repository>{status: RepositoryStatus.INITIALIZED});

                const promise = repositoryService.getRepository()
                    .pipe(take(2), toArray())
                    .toPromise()
                    .then((actual: Repository[]) => {
                        expect(actual).toEqual([firstExpected, secondExpected]);
                    });

                httpMock.expectOne('/api/repository').flush(secondExpected);
                httpMock.verify();

                return promise;
            }
        )
    );

    it('should get latest repository',
        inject(
            [HttpTestingController, RepositoryService],
            async (
                httpMock: HttpTestingController,
                repositoryService: RepositoryService
            ) => {
                const firstExpected = new Repository(<Repository>{status: RepositoryStatus.NOT_INITIALIZED});
                const secondExpected = new Repository(<Repository>{status: RepositoryStatus.INITIALIZED});

                const firstPromise = repositoryService.getRepository()
                    .pipe(take(2), toArray())
                    .toPromise()
                    .then(actual => {
                        expect(actual).toEqual([firstExpected, secondExpected]);
                        return actual;
                    });

                httpMock.expectOne('/api/repository').flush(secondExpected);
                httpMock.verify();

                await firstPromise;

                const secondPromise = repositoryService.getRepository()
                    .pipe(take(1), toArray())
                    .toPromise()
                    .then(actual => {
                        expect(actual).toEqual([secondExpected]);
                        return actual;
                    });

                await secondPromise;
            }
        )
    );

    it('should get latest updated repository',
        inject(
            [HttpTestingController, RepositoryService],
            async (
                httpMock: HttpTestingController,
                repositoryService: RepositoryService
            ) => {
                const firstExpected = new Repository(<Repository>{status: RepositoryStatus.NOT_INITIALIZED});
                const secondExpected = new Repository(<Repository>{status: RepositoryStatus.INITIALIZING});
                const thirdExpected = new Repository(<Repository>{status: RepositoryStatus.INITIALIZED});

                const promise = repositoryService.getRepository()
                    .pipe(take(3), toArray())
                    .toPromise()
                    .then(actual => {
                        expect(actual).toEqual([firstExpected, secondExpected, thirdExpected]);
                        return actual;
                    });

                httpMock.expectOne('/api/repository').flush(secondExpected);
                httpMock.verify();

                repositoryService.getRepository()
                    .pipe(take(2))
                    .toPromise()
                    .then(actual => {
                        eventService.subject.next(thirdExpected);
                    });

                return promise;
            }
        )
    );
});
