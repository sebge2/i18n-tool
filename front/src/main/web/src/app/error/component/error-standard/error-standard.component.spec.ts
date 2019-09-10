import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ErrorStandardComponent} from './error-standard.component';
import {ErrorMessageComponent} from "../error-message/error-message.component";
import {MainMessageComponent} from "../../../core/shared/component/main-message/main-message.component";
import {RouterModule} from "@angular/router";

describe('ErrorStandardComponent', () => {
    let component: ErrorStandardComponent;
    let fixture: ComponentFixture<ErrorStandardComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [RouterModule.forRoot([])],
            declarations: [ErrorStandardComponent, ErrorMessageComponent, MainMessageComponent],
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(ErrorStandardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
