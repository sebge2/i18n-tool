import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkspaceTableComponent} from './workspace-table.component';
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {HttpClientModule} from "@angular/common/http";
import {CoreEventModule} from "../../../core/event/core-event.module";
import {CoreUiModule} from "../../../core/ui/core-ui.module";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {Workspace} from "../../../translations/model/workspace.model";
import {WorkspaceService} from "../../../translations/service/workspace.service";

describe('WorkspaceTableComponent', () => {
    let component: WorkspaceTableComponent;
    let fixture: ComponentFixture<WorkspaceTableComponent>;
    let workspaceService: MockWorkspaceService;

    class MockWorkspaceService {

        readonly subject: Subject<Workspace[]> = new BehaviorSubject([]);

        public getWorkspaces(): Observable<Workspace[]> {
            return this.subject;
        }

        public find(): Promise<any> {
            return Promise.resolve(null);
        }
    }

    beforeEach(async(() => {
        workspaceService = new MockWorkspaceService();

        TestBed
            .configureTestingModule({
                imports: [
                    TranslateModule.forRoot(),
                    CoreUiModule,
                    CoreSharedModule,
                    CoreEventModule,
                    HttpClientModule
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
            workspaceService.subject.next(
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
            workspaceService.subject.next(
                [
                    new Workspace(<Workspace>{id: 'abc', branch: 'master'}),
                    new Workspace(<Workspace>{id: 'def', branch: 'release/2019.9'})
                ]
            );

            fixture.detectChanges();
            fixture.whenStable().then(() => {
                spyOn(workspaceService, 'find').and.returnValue(Promise.resolve());

                fixture.debugElement.nativeElement.querySelector('#findButton').click();

                expect(workspaceService.find).toHaveBeenCalled();
            });
        });

    // TODO erase workspace
});
