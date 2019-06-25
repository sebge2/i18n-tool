import {inject, TestBed} from '@angular/core/testing';

import {LogoutGuard} from './logout.guard';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule} from "@angular/router";
import {AuthenticationService} from "../authentication.service";

describe('LogoutGuard', () => {
    let authenticationService: AuthenticationService;

    beforeEach(() => {
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);

        TestBed
            .configureTestingModule({
                imports: [
                    HttpClientModule,
                    RouterModule.forRoot([])
                ],
                providers: [
                    LogoutGuard,
                    {provide: AuthenticationService, useValue: authenticationService}
                ]
            });
    });

    it('should ...', inject([LogoutGuard], (guard: LogoutGuard) => {
        expect(guard).toBeTruthy();
    }));
});
