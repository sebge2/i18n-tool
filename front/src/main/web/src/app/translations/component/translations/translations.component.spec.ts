import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationsComponent } from './translations.component';
import { TranslationsSearchBarComponent } from '../translations-search-bar/translations-search-bar.component';
import { TranslationsTableComponent } from '../translations-table/translations-table.component';
import { TranslateModule } from '@ngx-translate/core';
import { CoreSharedModule } from '@i18n-core-shared';
import { TranslationCriterionSelectorComponent } from '../translations-search-bar/translation-criterion-selector/translation-criterion-selector.component';
import { TranslationEditingCellComponent } from '../translations-table/translation-editing-cell/translation-editing-cell.component';
import { CoreEventModule } from '@i18n-core-event';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BehaviorSubject } from 'rxjs';
import { WorkspaceService } from '@i18n-core-translation';
import { Workspace } from '@i18n-core-translation';
import { AuthenticationService } from '@i18n-core-auth';
import { CoreAuthModule } from '@i18n-core-auth';
import { AuthenticatedUser } from '@i18n-core-auth';
import { ALL_USER_ROLES } from '@i18n-core-auth';
import { WorkspaceSelectorComponent } from '@i18n-core-translation';
import { TranslationLocaleSelectorComponent } from '@i18n-core-translation';

describe('TranslationsComponent', () => {
  let component: TranslationsComponent;
  let fixture: ComponentFixture<TranslationsComponent>;

  let workspaceService: WorkspaceService;
  let workspaces: BehaviorSubject<Workspace[]>;

  let user: BehaviorSubject<AuthenticatedUser> = new BehaviorSubject<AuthenticatedUser>(
    new AuthenticatedUser(ALL_USER_ROLES)
  );
  let authenticationService: AuthenticationService;

  beforeEach(async(() => {
    workspaceService = jasmine.createSpyObj('workspaceService', ['getWorkspaces']);
    workspaces = new BehaviorSubject([]);
    workspaceService.getWorkspaces = jasmine.createSpy().and.returnValue(workspaces);

    authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
    authenticationService.currentAuthenticatedUser = jasmine.createSpy().and.returnValue(user);

    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot(), BrowserAnimationsModule, CoreAuthModule, CoreSharedModule, CoreEventModule],
      providers: [
        { provide: WorkspaceService, useValue: workspaceService },
        { provide: AuthenticationService, useValue: authenticationService },
      ],
      declarations: [
        TranslationsComponent,
        TranslationsSearchBarComponent,
        TranslationsTableComponent,
        WorkspaceSelectorComponent,
        TranslationLocaleSelectorComponent,
        TranslationCriterionSelectorComponent,
        TranslationEditingCellComponent,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TranslationsComponent);
    component = fixture.componentInstance;
  }));

  xit('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy(); // TODO issue-125
  });
});
