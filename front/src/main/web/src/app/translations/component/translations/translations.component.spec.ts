import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TranslationsComponent} from './translations.component';
import {TranslationsSearchBarComponent} from "../translations-search-bar/translations-search-bar.component";
import {TranslationsTableComponent} from "../translations-table/translations-table.component";
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {TranslationCriterionSelectorComponent} from "../translations-search-bar/translation-criterion-selector/translation-criterion-selector.component";
import {TranslationEditingCellComponent} from "../translations-table/translation-editing-cell/translation-editing-cell.component";
import {CoreEventModule} from "../../../core/event/core-event.module";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {BehaviorSubject} from "rxjs";
import {WorkspaceService} from "../../service/workspace.service";
import {Workspace} from "../../model/workspace/workspace.model";
import {AuthenticationService} from "../../../core/auth/service/authentication.service";
import {CoreAuthModule} from "../../../core/auth/core-auth.module";
import {AuthenticatedUser} from "../../../core/auth/model/authenticated-user.model";
import {ALL_USER_ROLES} from "../../../core/auth/model/user-role.model";
import {WorkspaceSelectorComponent} from "../../../core/translation/component/workspace-selector/workspace-selector.component";
import {TranslationLocaleSelectorComponent} from "../../../core/translation/component/translation-locale-selector/translation-locale-selector.component";

describe('TranslationsComponent', () => {
    let component: TranslationsComponent;
    let fixture: ComponentFixture<TranslationsComponent>;

    let workspaceService: WorkspaceService;
    let workspaces: BehaviorSubject<Workspace[]>;

    let user: BehaviorSubject<AuthenticatedUser> = new BehaviorSubject<AuthenticatedUser>(new AuthenticatedUser(ALL_USER_ROLES));
    let authenticationService: AuthenticationService;

    beforeEach(async(() => {
        workspaceService = jasmine.createSpyObj('workspaceService', ['getWorkspaces']);
        workspaces = new BehaviorSubject([]);
        workspaceService.getWorkspaces = jasmine.createSpy().and.returnValue(workspaces);

        authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
        authenticationService.currentAuthenticatedUser = jasmine.createSpy().and.returnValue(user);

        TestBed
            .configureTestingModule({
                imports: [
                    TranslateModule.forRoot(),
                    BrowserAnimationsModule,
                    CoreAuthModule,
                    CoreSharedModule,
                    CoreEventModule
                ],
                providers: [
                    {provide: WorkspaceService, useValue: workspaceService},
                    {provide: AuthenticationService, useValue: authenticationService}
                ],
                declarations: [
                    TranslationsComponent,
                    TranslationsSearchBarComponent,
                    TranslationsTableComponent,
                    WorkspaceSelectorComponent,
                    TranslationLocaleSelectorComponent,
                    TranslationCriterionSelectorComponent,
                    TranslationEditingCellComponent,
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(TranslationsComponent);
        component = fixture.componentInstance;
    }));

    xit('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
