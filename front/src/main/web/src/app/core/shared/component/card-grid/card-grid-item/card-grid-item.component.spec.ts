import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CardGridItemComponent } from './card-grid-item.component';

describe('CardGridItemComponent', () => {
  let component: CardGridItemComponent;
  let fixture: ComponentFixture<CardGridItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CardGridItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CardGridItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
