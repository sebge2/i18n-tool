import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardSelectorComponent } from './card-selector.component';

describe('CardSelectorComponent', () => {
  let component: CardSelectorComponent;
  let fixture: ComponentFixture<CardSelectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CardSelectorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CardSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
