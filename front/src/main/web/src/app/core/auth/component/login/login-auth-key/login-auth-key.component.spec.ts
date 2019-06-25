import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginAuthKeyComponent} from './login-auth-key.component';
import {Router} from "@angular/router";
import {NotificationService} from "../../../../notification/service/notification.service";
import {AuthenticationService} from "../../../service/authentication.service";
import {CoreSharedModule} from "../../../../shared/core-shared-module";

describe('LoginAuthKeyComponent', () => {
    let component: LoginAuthKeyComponent;
    let fixture: ComponentFixture<LoginAuthKeyComponent>;
    let authenticationService: AuthenticationService;
    let notificationService: NotificationService;
    let router: Router;

    beforeEach(async(() => {
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);
        router = jasmine.createSpyObj('router', ['navigate']);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreSharedModule
                ],
                declarations: [
                    LoginAuthKeyComponent
                ],
                providers: [
                    {provide: NotificationService, useValue: notificationService},
                    {provide: AuthenticationService, useValue: authenticationService},
                    {provide: Router, useValue: router}
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(LoginAuthKeyComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
