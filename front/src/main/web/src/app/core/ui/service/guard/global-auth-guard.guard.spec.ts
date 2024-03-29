import {inject, TestBed} from '@angular/core/testing';

import {GlobalAuthGuard} from './global-auth-guard.service';
import {AuthenticationService} from '@i18n-core-auth';
import {BehaviorSubject} from 'rxjs';
import {Router} from '@angular/router';
import {AuthenticatedUser} from '@i18n-core-auth';

describe('GlobalAuthGuard', () => {
    let authenticationService: AuthenticationService;
    let currentUser: BehaviorSubject<AuthenticatedUser>;
    let router: Router;

    beforeEach(() => {
        currentUser = new BehaviorSubject<AuthenticatedUser>(null);
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentAuthenticatedUser = jasmine.createSpy().and.returnValue(currentUser);

        router = jasmine.createSpyObj('router', ['navigate']);

        TestBed.configureTestingModule({
            imports: [],
            providers: [
                GlobalAuthGuard,
                {provide: AuthenticationService, useValue: authenticationService},
                {provide: Router, useValue: router},
            ],
        });
    });

    xit('should ...', inject([GlobalAuthGuard], (guard: GlobalAuthGuard) => {
        expect(guard).toBeTruthy();
    }));
});
