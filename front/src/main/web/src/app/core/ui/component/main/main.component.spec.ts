import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MainComponent} from './main.component';
import {CoreSharedModule} from "../../../shared/core-shared-module";
import {RouterModule} from "@angular/router";
import {HttpClientModule} from "@angular/common/http";
import {CoreEventModule} from "../../../event/core-event.module";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {CoreUiModule} from "../../core-ui.module";

describe('MainComponent', () => {
    let component: MainComponent;
    let fixture: ComponentFixture<MainComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                BrowserAnimationsModule,
                CoreSharedModule,
                CoreUiModule,
                CoreEventModule,
                RouterModule.forRoot([]),
                HttpClientModule
            ],
            declarations: [],
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(MainComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
