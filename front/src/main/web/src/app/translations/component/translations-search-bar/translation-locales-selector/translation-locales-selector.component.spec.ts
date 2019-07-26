import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationLocalesSelectorComponent } from './translation-locales-selector.component';

describe('TranslationLocalesSelectorComponent', () => {
  let component: TranslationLocalesSelectorComponent;
  let fixture: ComponentFixture<TranslationLocalesSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationLocalesSelectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationLocalesSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
