import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkspaceTableComponent} from './workspace-table.component';
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {HttpClientModule} from "@angular/common/http";
import {CoreEventModule} from "../../../core/event/core-event.module";
import {CoreUiModule} from "../../../core/ui/core-ui.module";

describe('WorkspaceTableComponent', () => {
    let component: WorkspaceTableComponent;
    let fixture: ComponentFixture<WorkspaceTableComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                TranslateModule.forRoot(),
                CoreUiModule,
                CoreSharedModule,
                CoreEventModule,
                HttpClientModule
            ],
            declarations: [WorkspaceTableComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(WorkspaceTableComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
