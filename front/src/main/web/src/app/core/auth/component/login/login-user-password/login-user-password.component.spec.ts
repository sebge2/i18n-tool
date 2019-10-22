import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginUserPasswordComponent} from './login-user-password.component';
import {Router} from "@angular/router";
import {CoreUiModule} from "../../../../ui/core-ui.module";
import {HttpClientModule} from "@angular/common/http";
import {AuthenticationService} from "../../../service/authentication.service";
import {NotificationService} from "../../../../notification/service/notification.service";

describe('LoginUserPasswordComponent', () => {
    let component: LoginUserPasswordComponent;
    let fixture: ComponentFixture<LoginUserPasswordComponent>;
    let authenticationService: AuthenticationService;
    let notificationService: NotificationService;
    let router: Router;

    beforeEach(async(() => {
        router = jasmine.createSpyObj('router', ['navigate']);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreUiModule
                ],
                declarations: [
                    LoginUserPasswordComponent
                ],
                providers: [
                    {provide: NotificationService, useValue: notificationService},
                    {provide: AuthenticationService, useValue: authenticationService},
                    {provide: Router, useValue: router}
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(LoginUserPasswordComponent);
        component = fixture.componentInstance;
    }));
    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
