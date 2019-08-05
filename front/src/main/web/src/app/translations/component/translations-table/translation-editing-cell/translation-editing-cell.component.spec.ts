import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationEditingCellComponent } from './translation-editing-cell.component';

describe('TranslationEditingCellComponent', () => {
  let component: TranslationEditingCellComponent;
  let fixture: ComponentFixture<TranslationEditingCellComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationEditingCellComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationEditingCellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
