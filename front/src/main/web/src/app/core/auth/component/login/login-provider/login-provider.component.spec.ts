import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginProviderComponent} from './login-provider.component';
import {CoreUiModule} from "../../../../ui/core-ui.module";

describe('LoginProviderComponent', () => {
    let component: LoginProviderComponent;
    let fixture: ComponentFixture<LoginProviderComponent>;

    beforeEach(async(() => {
        TestBed
            .configureTestingModule({
                imports: [
                    CoreUiModule
                ],
                declarations: [
                    LoginProviderComponent
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(LoginProviderComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
