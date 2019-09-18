import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdminComponent} from './admin.component';
import {RepositoryInitializerComponent} from "../repository-initializer/repository-initializer.component";
import {WorkspaceTableComponent} from "../workspace-table/workspace-table.component";
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {HttpClientModule} from "@angular/common/http";
import {CoreEventModule} from "../../../core/event/core-event.module";
import {CoreUiModule} from "../../../core/ui/core-ui.module";

describe('AdminComponent', () => {
    let component: AdminComponent;
    let fixture: ComponentFixture<AdminComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                CoreUiModule,
                CoreSharedModule,
                CoreEventModule,
                HttpClientModule,
                TranslateModule.forRoot()
            ],
            declarations: [
                AdminComponent,
                RepositoryInitializerComponent,
                WorkspaceTableComponent
            ]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AdminComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
