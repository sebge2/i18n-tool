import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkspaceSelectorComponent} from './workspace-selector.component';
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../../core/shared/core-shared-module";
import {CoreEventModule} from "../../../../core/event/core-event.module";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {WorkspaceService} from "../../../service/workspace.service";
import {BehaviorSubject} from "rxjs";
import {Workspace} from "../../../model/workspace/workspace.model";

describe('WorkspaceSelectorComponent', () => {
    let component: WorkspaceSelectorComponent;
    let fixture: ComponentFixture<WorkspaceSelectorComponent>;
    let workspaceService: WorkspaceService;
    let workspaces: BehaviorSubject<Workspace[]>;

    beforeEach(async(() => {
        workspaceService = jasmine.createSpyObj('workspaceService', ['getWorkspaces']);
        workspaces = new BehaviorSubject([]);
        workspaceService.getWorkspaces = jasmine.createSpy().and.returnValue(workspaces);

        TestBed
            .configureTestingModule({
                imports: [
                    BrowserAnimationsModule,
                    CoreSharedModule,
                    CoreEventModule,
                    TranslateModule.forRoot()
                ],
                providers: [
                    {provide: WorkspaceService, useValue: workspaceService}
                ],
                declarations: [WorkspaceSelectorComponent]
            })
            .compileComponents();

        fixture = TestBed.createComponent(WorkspaceSelectorComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
