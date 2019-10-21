import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginUserPasswordComponent} from './login-user-password.component';
import {Router} from "@angular/router";
import {CoreUiModule} from "../../../../ui/core-ui.module";
import {HttpClientModule} from "@angular/common/http";

describe('LoginUserPasswordComponent', () => {
    let component: LoginUserPasswordComponent;
    let fixture: ComponentFixture<LoginUserPasswordComponent>;
    let router: Router;

    beforeEach(async(() => {
        router = jasmine.createSpyObj('router', ['navigate']);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreUiModule,
                    HttpClientModule,
                ],
                declarations: [
                    LoginUserPasswordComponent
                ],
                providers: [
                    {provide: Router, useValue: router}
                ],
            })
            .compileComponents();

        fixture = TestBed.createComponent(LoginUserPasswordComponent);
        component = fixture.componentInstance;
    }));
    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
