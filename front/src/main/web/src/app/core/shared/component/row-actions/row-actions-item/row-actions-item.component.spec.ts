import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RowActionsItemComponent } from './row-actions-item.component';

describe('RowActionsItemComponent', () => {
  let component: RowActionsItemComponent;
  let fixture: ComponentFixture<RowActionsItemComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RowActionsItemComponent],
    }).compileComponents();
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
