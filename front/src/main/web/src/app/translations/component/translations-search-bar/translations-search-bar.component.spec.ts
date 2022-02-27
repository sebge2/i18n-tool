import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TranslationsSearchBarComponent } from './translations-search-bar.component';
import { CoreSharedModule } from '@i18n-core-shared';
import { TranslationCriterionSelectorComponent } from './translation-criterion-selector/translation-criterion-selector.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateModule } from '@ngx-translate/core';
import { CoreEventModule } from '@i18n-core-event';
import { BehaviorSubject } from 'rxjs';
import { WorkspaceService } from '@i18n-core-translation';
import { Workspace } from '@i18n-core-translation';
import { TranslationLocaleSelectorComponent } from '@i18n-core-translation';
import { WorkspaceSelectorComponent } from '@i18n-core-translation';

describe('TranslationsSearchBarComponent', () => {
  let component: TranslationsSearchBarComponent;
  let fixture: ComponentFixture<TranslationsSearchBarComponent>;
  let workspaceService: WorkspaceService;
  let workspaces: BehaviorSubject<Workspace[]>;

  beforeEach(waitForAsync(() => {
    workspaceService = jasmine.createSpyObj('workspaceService', ['getWorkspaces']);
    workspaces = new BehaviorSubject([]);
    workspaceService.getWorkspaces = jasmine.createSpy().and.returnValue(workspaces);

    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule, CoreSharedModule, CoreEventModule, TranslateModule.forRoot()],
      providers: [{ provide: WorkspaceService, useValue: workspaceService }],
      declarations: [
        TranslationsSearchBarComponent,
        WorkspaceSelectorComponent,
        TranslationLocaleSelectorComponent,
        TranslationCriterionSelectorComponent,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TranslationsSearchBarComponent);
    component = fixture.componentInstance;
  }));

  xit('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy(); // TODO issue-125
  });
});
