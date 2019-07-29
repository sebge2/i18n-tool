import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationFlagSelectorComponent } from './translation-flag-selector.component';

describe('TranslationFlagSelectorComponent', () => {
  let component: TranslationFlagSelectorComponent;
  let fixture: ComponentFixture<TranslationFlagSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationFlagSelectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationFlagSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
