import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ScheduledTaskExecutionListComponent } from './scheduled-task-execution-list.component';

describe('ScheduledTaskExecutionListComponent', () => {
  let component: ScheduledTaskExecutionListComponent;
  let fixture: ComponentFixture<ScheduledTaskExecutionListComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ScheduledTaskExecutionListComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScheduledTaskExecutionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
