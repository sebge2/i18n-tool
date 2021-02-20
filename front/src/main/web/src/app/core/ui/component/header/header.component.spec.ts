import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {HeaderComponent} from './header.component';
import {CoreEventModule} from "../../../event/core-event.module";
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {BehaviorSubject} from "rxjs";
import {Router} from "@angular/router";
import {AuthenticatedUser} from "../../../auth/model/authenticated-user.model";
import {CoreSharedModule} from "../../../shared/core-shared-module";

describe('HeaderComponent', () => {
    let component: HeaderComponent;
    let fixture: ComponentFixture<HeaderComponent>;
    let authenticationService: AuthenticationService;
    let currentUser: BehaviorSubject<AuthenticatedUser>;
    let router: Router;

    beforeEach(async(() => {
        currentUser = new BehaviorSubject<AuthenticatedUser>(null);
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentAuthenticatedUser = jasmine.createSpy().and.returnValue(currentUser);
        router = jasmine.createSpyObj('router', ['navigate']);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreSharedModule,
                    CoreEventModule
                ],
                declarations: [HeaderComponent],
                providers: [
                    {provide: AuthenticationService, useValue: authenticationService},
                    {provide: Router, useValue: router}
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(HeaderComponent);
        component = fixture.componentInstance;
    }));

    xit('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO issue-125
    });
});
