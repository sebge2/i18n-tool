import {getTestBed, inject, TestBed} from '@angular/core/testing';

import {AuthenticationService} from './authentication.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {User} from "../model/user.model";

describe('AuthenticationService', () => {
    let injector: TestBed;
    let service: AuthenticationService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [AuthenticationService]
        });

        injector = getTestBed();
        service = injector.get(AuthenticationService);
        httpMock = injector.get(HttpTestingController);
    });

    it('should get current user',
        inject(
            [HttpTestingController, AuthenticationService],
            async (
                httpMock: HttpTestingController,
                authService: AuthenticationService
            ) => {
                const expected = new User(<User>{id: 'abc'});

                const promise = authService.currentUser
                    .toPromise()
                    .then((actual: User) => {
                        expect(actual).toEqual(expected);
                    });

                httpMock.expectOne('/api/authentication/user').flush(expected);
                httpMock.verify();

                return promise;
            }
        )
    );
});
