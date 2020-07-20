import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationLocaleSelectorComponent } from './translation-locale-selector.component';

describe('TranslationLocaleSelectorComponent', () => {
  let component: TranslationLocaleSelectorComponent;
  let fixture: ComponentFixture<TranslationLocaleSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationLocaleSelectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationLocaleSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
