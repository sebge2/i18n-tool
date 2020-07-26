import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MainComponent} from './main.component';
import {CoreSharedModule} from "../../../shared/core-shared-module";
import {RouterModule} from "@angular/router";
import {CoreEventModule} from "../../../event/core-event.module";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {BehaviorSubject} from "rxjs";
import {AuthenticatedUser} from "../../../auth/model/authenticated-user.model";
import {HeaderComponent} from "../header/header.component";
import {MenuComponent} from "../menu/menu.component";
import {CoreAuthModule} from "../../../auth/core-auth.module";

describe('MainComponent', () => {
    let component: MainComponent;
    let fixture: ComponentFixture<MainComponent>;
    let authenticationService: AuthenticationService;
    let currentUser: BehaviorSubject<AuthenticatedUser>;

    beforeEach(async(() => {
        currentUser = new BehaviorSubject<AuthenticatedUser>(null);
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentAuthenticatedUser = jasmine.createSpy().and.returnValue(currentUser);

        TestBed
            .configureTestingModule({
                imports: [
                    BrowserAnimationsModule,
                    CoreSharedModule,
                    CoreAuthModule,
                    CoreEventModule,
                    RouterModule.forRoot([])
                ],
                declarations: [MainComponent, HeaderComponent, MenuComponent],
                providers: [
                    {provide: AuthenticationService, useValue: authenticationService}
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(MainComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
