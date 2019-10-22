import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {HeaderComponent} from './header.component';
import {CoreEventModule} from "../../../event/core-event.module";
import {CoreUiModule} from "../../core-ui.module";
import {AuthenticationService} from "../../../auth/service/authentication.service";
import {BehaviorSubject} from "rxjs";
import {User} from "../../../auth/model/user.model";

describe('HeaderComponent', () => {
    let component: HeaderComponent;
    let fixture: ComponentFixture<HeaderComponent>;
    let authenticationService: AuthenticationService;
    let currentUser: BehaviorSubject<User>;

    beforeEach(async(() => {
        currentUser = new BehaviorSubject<User>(null);
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentUser = jasmine.createSpy().and.returnValue(currentUser);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreUiModule,
                    CoreEventModule
                ],
                providers: [
                    {provide: AuthenticationService, useValue: authenticationService}
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(HeaderComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
