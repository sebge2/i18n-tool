import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkspaceTableComponent} from './workspace-table.component';
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {CoreEventModule} from "../../../core/event/core-event.module";
import {CoreUiModule} from "../../../core/ui/core-ui.module";
import {BehaviorSubject} from "rxjs";
import {Workspace} from "../../../translations/model/workspace.model";
import {WorkspaceService} from "../../../translations/service/workspace.service";

describe('WorkspaceTableComponent', () => {
    let component: WorkspaceTableComponent;
    let fixture: ComponentFixture<WorkspaceTableComponent>;
    let workspaceService: WorkspaceService;
    let workspaces: BehaviorSubject<Workspace[]>;

    beforeEach(async(() => {
        workspaceService = jasmine.createSpyObj('workspaceService', ['getWorkspaces', 'find']);

        workspaces = new BehaviorSubject<Workspace[]>([]);

        workspaceService.getWorkspaces = jasmine.createSpy().and.returnValue(workspaces);
        workspaceService.find = jasmine.createSpy().and.returnValue(Promise.resolve());

        TestBed
            .configureTestingModule({
                imports: [
                    TranslateModule.forRoot(),
                    CoreUiModule,
                    CoreSharedModule,
                    CoreEventModule
                ],
                providers: [
                    {provide: WorkspaceService, useValue: workspaceService}
                ],
                declarations: [WorkspaceTableComponent]
            })
            .compileComponents();

        fixture = TestBed.createComponent(WorkspaceTableComponent);
        component = fixture.componentInstance;
    }));

    it('should display workspaces',
        async () => {
            workspaces.next(
                [
                    new Workspace(<Workspace>{id: 'abc', branch: 'master'}),
                    new Workspace(<Workspace>{id: 'def', branch: 'release/2019.9'})
                ]
            );

            fixture.detectChanges();
            fixture.whenStable().then(() => {
                const rows = fixture.debugElement.nativeElement.querySelectorAll('mat-row');

                expect(rows.length).toBe(2);
                expect(rows[0].querySelector('mat-cell').textContent).toContain('master');
                expect(rows[1].querySelector('mat-cell').textContent).toContain('release/2019.9');
            });
        });

    it('should find when clicked',
        async () => {
            workspaces.next(
                [
                    new Workspace(<Workspace>{id: 'abc', branch: 'master'}),
                    new Workspace(<Workspace>{id: 'def', branch: 'release/2019.9'})
                ]
            );

            fixture.detectChanges();
            fixture.whenStable().then(() => {
                fixture.debugElement.nativeElement.querySelector('#findButton').click();

                expect(workspaceService.find).toHaveBeenCalled();
            });
        });

    // TODO erase workspace
});
