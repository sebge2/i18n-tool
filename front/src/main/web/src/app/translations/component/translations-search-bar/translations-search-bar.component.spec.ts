import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationsSearchBarComponent } from './translations-search-bar.component';

describe('TranslationsSearchBarComponent', () => {
  let component: TranslationsSearchBarComponent;
  let fixture: ComponentFixture<TranslationsSearchBarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationsSearchBarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationsSearchBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
