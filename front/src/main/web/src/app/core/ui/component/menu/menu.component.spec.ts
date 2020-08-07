import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MenuComponent} from './menu.component';
import {CoreSharedModule} from "../../../shared/core-shared-module";
import {CoreAuthModule} from "../../../auth/core-auth.module";
import {RouterTestingModule} from "@angular/router/testing";
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {AuthenticatedUser} from "../../../auth/model/authenticated-user.model";
import {BehaviorSubject} from "rxjs";
import {ALL_USER_ROLES, UserRole} from "../../../auth/model/user-role.model";

describe('MenuComponent', () => {
    let component: MenuComponent;
    let fixture: ComponentFixture<MenuComponent>;

    let user: BehaviorSubject<AuthenticatedUser> = new BehaviorSubject<AuthenticatedUser>(new AuthenticatedUser(ALL_USER_ROLES));
    let authenticationService: AuthenticationService;

    beforeEach(async(() => {
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentAuthenticatedUser = jasmine.createSpy().and.returnValue(user);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreSharedModule,
                    CoreAuthModule,
                    RouterTestingModule
                ],
                declarations: [MenuComponent],
                providers: [
                    {provide: AuthenticationService, useValue: authenticationService}
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(MenuComponent);
        component = fixture.componentInstance;
    }));

    it('should have all rights', () => {
        user.next(new AuthenticatedUser(ALL_USER_ROLES));

        fixture.detectChanges();

        expect(fixture.nativeElement.querySelector('#menuAdmin')).not.toBeNull();
    });

    it('should have limited rights', () => {
        user.next(new AuthenticatedUser([UserRole.MEMBER_OF_ORGANIZATION]));

        fixture.detectChanges();

        expect(fixture.nativeElement.querySelector('#menuAdmin')).toBeNull();
    });
});
