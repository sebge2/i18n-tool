import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ScheduledTasksComponent } from './scheduled-tasks.component';

describe('ScheduledTasksComponent', () => {
  let component: ScheduledTasksComponent;
  let fixture: ComponentFixture<ScheduledTasksComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ScheduledTasksComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScheduledTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
