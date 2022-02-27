import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RowActionsElementComponent } from './row-actions-element.component';

describe('RowActionsElementComponent', () => {
  let component: RowActionsElementComponent;
  let fixture: ComponentFixture<RowActionsElementComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RowActionsElementComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RowActionsElementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
