import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LogoutComponent} from './logout.component';
import {AuthenticationService} from "../../service/authentication.service";
import {CoreSharedModule} from "../../../shared/core-shared-module";

describe('LogoutComponent', () => {
    let component: LogoutComponent;
    let fixture: ComponentFixture<LogoutComponent>;
    let authenticationService: AuthenticationService;

    beforeEach(async(() => {
        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreSharedModule
                ],
                declarations: [
                    LogoutComponent
                ],
                providers: [
                    {provide: AuthenticationService, useValue: authenticationService}
                ],
            })
            .compileComponents();

        fixture = TestBed.createComponent(LogoutComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
