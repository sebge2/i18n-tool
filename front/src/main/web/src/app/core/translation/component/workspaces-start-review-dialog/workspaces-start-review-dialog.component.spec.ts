import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkspacesStartReviewDialogComponent } from './workspaces-start-review-dialog.component';

describe('WorkspacesStartReviewDialogComponent', () => {
  let component: WorkspacesStartReviewDialogComponent;
  let fixture: ComponentFixture<WorkspacesStartReviewDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [WorkspacesStartReviewDialogComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkspacesStartReviewDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
