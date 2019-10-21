import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginAuthKeyComponent} from './login-auth-key.component';
import {CoreUiModule} from "../../../../ui/core-ui.module";
import {HttpClientModule} from "@angular/common/http";
import {Router} from "@angular/router";

describe('LoginAuthKeyComponent', () => {
    let component: LoginAuthKeyComponent;
    let fixture: ComponentFixture<LoginAuthKeyComponent>;
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
                    LoginAuthKeyComponent
                ],
                providers: [
                    {provide: Router, useValue: router}
                ],
            })
            .compileComponents();

        fixture = TestBed.createComponent(LoginAuthKeyComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
