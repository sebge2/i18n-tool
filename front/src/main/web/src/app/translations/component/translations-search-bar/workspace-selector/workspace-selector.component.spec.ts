import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkspaceSelectorComponent} from './workspace-selector.component';
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../../core/shared/core-shared-module";
import {HttpClientModule} from "@angular/common/http";
import {CoreEventModule} from "../../../../core/event/core-event.module";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {CoreUiModule} from "../../../../core/ui/core-ui.module";

describe('WorkspaceSelectorComponent', () => {
    let component: WorkspaceSelectorComponent;
    let fixture: ComponentFixture<WorkspaceSelectorComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                BrowserAnimationsModule,
                CoreSharedModule,
                CoreUiModule,
                CoreEventModule,
                HttpClientModule,
                TranslateModule.forRoot()
            ],
            declarations: [WorkspaceSelectorComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(WorkspaceSelectorComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
