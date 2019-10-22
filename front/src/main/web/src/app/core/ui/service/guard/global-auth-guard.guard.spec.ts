import {inject, TestBed} from '@angular/core/testing';

import {GlobalAuthGuard} from './global-auth-guard.service';
import {CoreUiModule} from "../../core-ui.module";
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {BehaviorSubject} from "rxjs";
import {User} from "../../../auth/model/user.model";
import {Router} from "@angular/router";

describe('GlobalAuthGuard', () => {
    let authenticationService: AuthenticationService;
    let currentUser: BehaviorSubject<User>;
    let router: Router;

    beforeEach(() => {
        currentUser = new BehaviorSubject<User>(null);
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentUser = jasmine.createSpy().and.returnValue(currentUser);

        router = jasmine.createSpyObj('router', ['navigate']);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreUiModule
                ],
                providers: [
                    GlobalAuthGuard,
                    {provide: AuthenticationService, useValue: authenticationService},
                    {provide: Router, useValue: router}
                ]
            });
    });

    it('should ...', inject([GlobalAuthGuard], (guard: GlobalAuthGuard) => {
        expect(guard).toBeTruthy();
    }));
});
