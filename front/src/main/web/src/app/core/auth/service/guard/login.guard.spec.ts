import {inject, TestBed} from '@angular/core/testing';

import {LoginGuard} from './login.guard';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule} from "@angular/router";
import {CoreUiModule} from "../../../ui/core-ui.module";
import {AuthenticationService} from "../authentication.service";

describe('LoginGuard', () => {

    let authenticationService: AuthenticationService;

    beforeEach(() => {
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);

        TestBed
            .configureTestingModule({
                imports: [
                    HttpClientModule,
                    RouterModule.forRoot([]),
                    CoreUiModule
                ],
                providers: [
                    LoginGuard,
                    {provide: AuthenticationService, useValue: authenticationService}
                ]
            });
    });

    it('should ...', inject([LoginGuard], (guard: LoginGuard) => {
        expect(guard).toBeTruthy();
    }));
});
