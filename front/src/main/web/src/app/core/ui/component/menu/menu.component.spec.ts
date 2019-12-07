import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MenuComponent} from './menu.component';
import {CoreSharedModule} from "../../../shared/core-shared-module";
import {CoreAuthModule} from "../../../auth/core-auth.module";
import {RouterTestingModule} from "@angular/router/testing";
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {AuthenticatedUser} from "../../../auth/model/authenticated-user.model";
import {BehaviorSubject} from "rxjs";
import {ALL_USER_ROLES} from "../../../auth/model/user-role.model";

describe('MenuComponent', () => {
    let component: MenuComponent;
    let fixture: ComponentFixture<MenuComponent>;
    // let router: Router;
    // let activatedRoute: ActivatedRoute;

    let user: BehaviorSubject<AuthenticatedUser> = new BehaviorSubject<AuthenticatedUser>(new AuthenticatedUser(<AuthenticatedUser>{sessionRoles: ALL_USER_ROLES}));
    let authenticationService: AuthenticationService;

    beforeEach(async(() => {
        // router = jasmine.createSpyObj('router', ['navigate']);
        // activatedRoute = new ActivatedRoute();

        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentUser = jasmine.createSpy().and.returnValue(user);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreSharedModule,
                    CoreAuthModule,
                    RouterTestingModule
                ],
                declarations: [MenuComponent],
                providers: [
                    // {provide: Router, useValue: router},
                    // {provide: ActivatedRoute, useValue: activatedRoute},
                    {provide: AuthenticationService, useValue: authenticationService}
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(MenuComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
