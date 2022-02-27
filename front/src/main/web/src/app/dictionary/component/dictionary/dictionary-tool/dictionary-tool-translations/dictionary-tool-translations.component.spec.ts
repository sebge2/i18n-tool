import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DictionaryToolTranslationsComponent } from './dictionary-tool-translations.component';

describe('DictionaryToolTranslationsComponent', () => {
  let component: DictionaryToolTranslationsComponent;
  let fixture: ComponentFixture<DictionaryToolTranslationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DictionaryToolTranslationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DictionaryToolTranslationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
