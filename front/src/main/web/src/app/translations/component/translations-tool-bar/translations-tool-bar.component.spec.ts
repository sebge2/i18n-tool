import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationsToolBarComponent } from './translations-tool-bar.component';

describe('TranslationsToolBarComponent', () => {
  let component: TranslationsToolBarComponent;
  let fixture: ComponentFixture<TranslationsToolBarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TranslationsToolBarComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationsToolBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
