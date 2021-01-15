import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {Error403Component} from './error403.component';
import {ErrorMessageComponent} from "../error-message/error-message.component";
import {MainMessageComponent} from "../../../core/shared/component/main-message/main-message.component";

describe('Error403Component', () => {
    let component: Error403Component;
    let fixture: ComponentFixture<Error403Component>;

    beforeEach(async(() => {
        TestBed
            .configureTestingModule({
                declarations: [
                    Error403Component,
                    ErrorMessageComponent,
                    MainMessageComponent
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(Error403Component);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO issue-125
    });
});
