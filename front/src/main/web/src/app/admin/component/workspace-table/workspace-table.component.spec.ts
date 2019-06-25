import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {WorkspaceTableComponent} from './workspace-table.component';
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {CoreEventModule} from "../../../core/event/core-event.module";
import {BehaviorSubject} from "rxjs";
import {Workspace} from "../../../translations/model/workspace.model";
import {WorkspaceService} from "../../../translations/service/workspace.service";
import {AuthenticationService} from "../../../core/auth/service/authentication.service";
import {CoreAuthModule} from "../../../core/auth/core-auth.module";
import {ALL_USER_ROLES} from "../../../core/auth/model/user-role.model";
import {AuthenticatedUser} from "../../../core/auth/model/authenticated-user.model";
import {By} from "@angular/platform-browser";
import { of } from 'rxjs';
import {delay} from "rxjs/operators";

describe('WorkspaceTableComponent', () => {
    let component: WorkspaceTableComponent;
    let fixture: ComponentFixture<WorkspaceTableComponent>;

    let workspaceService: WorkspaceService;
    let workspaces: BehaviorSubject<Workspace[]>;

    let user: BehaviorSubject<AuthenticatedUser> = new BehaviorSubject<AuthenticatedUser>(new AuthenticatedUser(<AuthenticatedUser>{sessionRoles: ALL_USER_ROLES}));
    let authenticationService: AuthenticationService;

    beforeEach(async(() => {
        workspaceService = jasmine.createSpyObj('workspaceService', ['getWorkspaces', 'find']);

        workspaces = new BehaviorSubject<Workspace[]>([]);

        workspaceService.getWorkspaces = jasmine.createSpy().and.returnValue(workspaces);
        workspaceService.find = jasmine.createSpy('find').and.returnValue(of ([]).pipe(delay(1000)).toPromise());

        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentUser = jasmine.createSpy('currentUser').and.returnValue(user);

        TestBed
            .configureTestingModule({
                imports: [
                    TranslateModule.forRoot(),
                    CoreSharedModule,
                    CoreEventModule,
                    CoreAuthModule
                ],
                providers: [
                    {provide: WorkspaceService, useValue: workspaceService},
                    {provide: AuthenticationService, useValue: authenticationService}
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
        fakeAsync( () => {
            workspaces.next(
                [
                    new Workspace(<Workspace>{id: 'abc', branch: 'master'}),
                    new Workspace(<Workspace>{id: 'def', branch: 'release/2019.9'})
                ]
            );

            fixture.detectChanges();

            fixture.whenStable().then(() => {
                expect(fixture.debugElement.query(By.css('.fa-spin'))).toBeNull();

                fixture.debugElement.nativeElement.querySelector('#findButton').click();

                fixture.detectChanges();

                expect(fixture.debugElement.query(By.css('.fa-spin'))).not.toBeNull();

                tick(1000);
                fixture.detectChanges();

                expect(fixture.debugElement.query(By.css('.fa-spin'))).toBeNull();

                expect(workspaceService.find).toHaveBeenCalled();
            });
        }));

    // TODO erase workspace not admin
    // TODO erase workspace
});
