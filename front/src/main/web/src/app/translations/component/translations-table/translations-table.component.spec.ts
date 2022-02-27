import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { CoreSharedModule } from '@i18n-core-shared';
import { TranslationEditingCellComponent } from './translation-editing-cell/translation-editing-cell.component';
import { TranslationsTableComponent } from './translations-table.component';
import { AuthenticationService } from '@i18n-core-auth';
import { TranslationService } from '@i18n-core-translation';

describe('TranslationsTableComponent', () => {
  let component: TranslationsTableComponent;
  let fixture: ComponentFixture<TranslationsTableComponent>;
  let authenticationService: AuthenticationService;
  let translationsService: TranslationService;

  beforeEach(waitForAsync(() => {
    authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
    translationsService = jasmine.createSpyObj('translationService', ['getTranslations']);

    TestBed.configureTestingModule({
      imports: [CoreSharedModule, TranslateModule.forRoot()],
      declarations: [TranslationsTableComponent, TranslationEditingCellComponent],
      providers: [
        { provide: AuthenticationService, useValue: authenticationService },
        { provide: TranslationService, useValue: translationsService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TranslationsTableComponent);
    component = fixture.componentInstance;
  }));

  xit('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy(); // TODO issue-125
  });
});
