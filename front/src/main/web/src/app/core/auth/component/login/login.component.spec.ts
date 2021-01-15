import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginComponent} from './login.component';
import {LoginUserPasswordComponent} from "./login-user-password/login-user-password.component";
import {LoginProviderComponent} from "./login-provider/login-provider.component";
import {CoreSharedModule} from "../../../shared/core-shared-module";
import {Router} from "@angular/router";
import {NotificationService} from "../../../notification/service/notification.service";
import {AuthenticationService} from "../../service/authentication.service";
import {TranslateService} from '@ngx-translate/core';

describe('LoginComponent', () => {
    let component: LoginComponent;
    let fixture: ComponentFixture<LoginComponent>;
    let authenticationService: AuthenticationService;
    let notificationService: NotificationService;
    let translationService: TranslateService;
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
                    LoginComponent,
                    LoginProviderComponent,
                    LoginUserPasswordComponent
                ],
                providers: [
                    {provide: NotificationService, useValue: notificationService},
                    {provide: AuthenticationService, useValue: authenticationService},
                    {provide: Router, useValue: router},
                    {provide: TranslateService, useValue: translationService}
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(LoginComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO issue-125
    });
});
