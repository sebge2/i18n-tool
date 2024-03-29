import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TranslationLocaleSelectorComponent } from './translation-locale-selector.component';

describe('TranslationLocaleSelectorComponent', () => {
  let component: TranslationLocaleSelectorComponent;
  let fixture: ComponentFixture<TranslationLocaleSelectorComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [TranslationLocaleSelectorComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationLocaleSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
