import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TranslationCriterionSelectorComponent } from './translation-criterion-selector.component';
import { CoreSharedModule } from '@i18n-core-shared';
import { TranslateModule } from '@ngx-translate/core';

describe('TranslationCriterionSelectorComponent', () => {
  let component: TranslationCriterionSelectorComponent;
  let fixture: ComponentFixture<TranslationCriterionSelectorComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [CoreSharedModule, TranslateModule.forRoot()],
      declarations: [TranslationCriterionSelectorComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TranslationCriterionSelectorComponent);
    component = fixture.componentInstance;
  }));

  xit('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy(); // TODO issue-125
  });
});
