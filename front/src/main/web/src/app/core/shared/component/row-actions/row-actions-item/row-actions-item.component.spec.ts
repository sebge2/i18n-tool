import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RowActionsItemComponent } from './row-actions-item.component';

describe('RowActionsItemComponent', () => {
  let component: RowActionsItemComponent;
  let fixture: ComponentFixture<RowActionsItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RowActionsItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RowActionsItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
