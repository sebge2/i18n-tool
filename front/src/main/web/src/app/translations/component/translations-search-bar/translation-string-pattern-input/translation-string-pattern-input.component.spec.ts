import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationStringPatternInputComponent } from './translation-string-pattern-input.component';

describe('TranslationKeyPatternInputComponent', () => {
  let component: TranslationStringPatternInputComponent;
  let fixture: ComponentFixture<TranslationStringPatternInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationStringPatternInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationStringPatternInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
