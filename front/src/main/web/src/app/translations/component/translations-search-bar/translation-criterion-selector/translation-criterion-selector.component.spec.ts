import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationCriterionSelectorComponent } from './translation-criterion-selector.component';

describe('TranslationCriterionSelectorComponent', () => {
  let component: TranslationCriterionSelectorComponent;
  let fixture: ComponentFixture<TranslationCriterionSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationCriterionSelectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationCriterionSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
