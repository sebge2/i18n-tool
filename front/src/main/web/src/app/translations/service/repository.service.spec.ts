import {getTestBed, inject, TestBed} from '@angular/core/testing';

import {RepositoryService} from './repository.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {CoreEventModule} from "../../core/event/core-event.module";
import {Repository} from "../model/repository.model";
import {RepositoryStatus} from "../model/repository-status.model";
import {take, toArray} from "rxjs/operators";

describe('RepositoryService', () => {
    let injector: TestBed;
    let service: RepositoryService;
    let httpMock: HttpTestingController;


    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule, CoreEventModule],
            providers: [RepositoryService]
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

                const mockReq = httpMock.expectOne('/api/repository');

                mockReq.flush(secondExpected);

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

                const mockReq = httpMock.expectOne('/api/repository');

                const firstPromise = repositoryService.getRepository()
                    .pipe(take(2), toArray())
                    .toPromise()
                    .then(actual => {
                        expect(actual).toEqual([firstExpected, secondExpected]);
                        return actual;
                    });

                mockReq.flush(secondExpected);
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

    // TODO subscribe and get update
});
