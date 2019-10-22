import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MainComponent} from './main.component';
import {CoreSharedModule} from "../../../shared/core-shared-module";
import {RouterModule} from "@angular/router";
import {CoreEventModule} from "../../../event/core-event.module";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {CoreUiModule} from "../../core-ui.module";
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {BehaviorSubject} from "rxjs";
import {User} from "../../../auth/model/user.model";

describe('MainComponent', () => {
    let component: MainComponent;
    let fixture: ComponentFixture<MainComponent>;
    let authenticationService: AuthenticationService;
    let currentUser: BehaviorSubject<User>;

    beforeEach(async(() => {
        currentUser = new BehaviorSubject<User>(null);
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentUser = jasmine.createSpy().and.returnValue(currentUser);

        TestBed
            .configureTestingModule({
                imports: [
                    BrowserAnimationsModule,
                    CoreSharedModule,
                    CoreUiModule,
                    CoreEventModule,
                    RouterModule.forRoot([])
                ],
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
