import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RowActionsComponent } from './row-actions.component';

describe('RowActionsComponent', () => {
  let component: RowActionsComponent;
  let fixture: ComponentFixture<RowActionsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RowActionsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RowActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
