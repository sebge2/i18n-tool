import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LogoutComponent} from './logout.component';
import {HttpClientModule} from "@angular/common/http";
import {CoreUiModule} from "../../../ui/core-ui.module";

describe('LogoutComponent', () => {
    let component: LogoutComponent;
    let fixture: ComponentFixture<LogoutComponent>;

    beforeEach(async(() => {
        TestBed
            .configureTestingModule({
                imports: [
                    CoreUiModule,
                    HttpClientModule
                ],
                declarations: [
                    LogoutComponent
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(LogoutComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
