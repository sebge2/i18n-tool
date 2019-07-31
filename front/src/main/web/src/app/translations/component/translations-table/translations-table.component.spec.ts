import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationsTableComponent } from './translations-table.component';

describe('TranslationsTableComponent', () => {
  let component: TranslationsTableComponent;
  let fixture: ComponentFixture<TranslationsTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationsTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
