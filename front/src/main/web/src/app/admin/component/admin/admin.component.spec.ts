import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdminComponent} from './admin.component';
import {RepositoryInitializerComponent} from "../repository-initializer/repository-initializer.component";
import {WorkspaceTableComponent} from "../workspace-table/workspace-table.component";
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {CoreEventModule} from "../../../core/event/core-event.module";
import {CoreUiModule} from "../../../core/ui/core-ui.module";
import {RepositoryService} from "../../../translations/service/repository.service";
import {BehaviorSubject} from "rxjs";
import {WorkspaceService} from "../../../translations/service/workspace.service";
import {Repository} from "../../../translations/model/repository.model";
import {RepositoryStatus} from "../../../translations/model/repository-status.model";
import {Workspace} from "../../../translations/model/workspace.model";
import {AuthenticationService} from "../../../core/auth/service/authentication.service";
import {ALL_USER_ROLES} from "../../../core/auth/model/user-role.model";
import {CoreAuthModule} from "../../../core/auth/core-auth.module";
import {AuthenticatedUser} from "../../../core/auth/model/authenticated-user.model";

describe('AdminComponent', () => {
    let component: AdminComponent;
    let fixture: ComponentFixture<AdminComponent>;
    let repositoryService: RepositoryService;
    let workspaceService: WorkspaceService;

    let user: BehaviorSubject<AuthenticatedUser> = new BehaviorSubject<AuthenticatedUser>(new AuthenticatedUser(<AuthenticatedUser>{sessionRoles: ALL_USER_ROLES}));
    let authenticationService: AuthenticationService;

    let workspaces: BehaviorSubject<Workspace[]>;
    let repository: BehaviorSubject<Repository>;

    beforeEach(async(() => {
        repositoryService = jasmine.createSpyObj('repositoryService', ['getRepository']);
        workspaceService = jasmine.createSpyObj('workspaceService', ['getWorkspaces']);

        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentUser = jasmine.createSpy().and.returnValue(user);

        workspaces = new BehaviorSubject([]);
        repository = new BehaviorSubject(
            new Repository(<Repository>{
                status: RepositoryStatus.NOT_INITIALIZED
            })
        );

        workspaceService.getWorkspaces = jasmine.createSpy().and.returnValue(workspaces);
        repositoryService.getRepository = jasmine.createSpy().and.returnValue(repository);

        TestBed
            .configureTestingModule({
                imports: [
                    CoreUiModule,
                    CoreSharedModule,
                    CoreEventModule,
                    CoreAuthModule,
                    TranslateModule.forRoot()
                ],
                declarations: [
                    AdminComponent,
                    RepositoryInitializerComponent,
                    WorkspaceTableComponent
                ],
                providers: [
                    {provide: RepositoryService, useValue: repositoryService},
                    {provide: WorkspaceService, useValue: workspaceService},
                    {provide: AuthenticationService, useValue: authenticationService}
                ],
            })
            .compileComponents();

        fixture = TestBed.createComponent(AdminComponent);
        component = fixture.componentInstance;
    }));

    it('should display everything ok', () => {
        repository.next(
            new Repository(<Repository>{
                status: RepositoryStatus.INITIALIZED
            })
        );

        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
