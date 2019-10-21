import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginComponent} from './login.component';
import {LoginAuthKeyComponent} from "./login-auth-key/login-auth-key.component";
import {LoginUserPasswordComponent} from "./login-user-password/login-user-password.component";
import {LoginProviderComponent} from "./login-provider/login-provider.component";
import {CoreUiModule} from "../../../ui/core-ui.module";
import {CoreSharedModule} from "../../../shared/core-shared-module";
import {HttpClientModule} from "@angular/common/http";
import {Router, RouterModule} from "@angular/router";

describe('LoginComponent', () => {
    let component: LoginComponent;
    let fixture: ComponentFixture<LoginComponent>;
    let router: Router;

    beforeEach(async(() => {
        router = jasmine.createSpyObj('router', ['navigate']);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreUiModule,
                    CoreSharedModule,
                    RouterModule,
                    HttpClientModule
                ],
                declarations: [
                    LoginComponent,
                    LoginProviderComponent,
                    LoginAuthKeyComponent,
                    LoginUserPasswordComponent
                ],
                providers: [
                    {provide: Router, useValue: router}
                ],
            })
            .compileComponents();

        fixture = TestBed.createComponent(LoginComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
