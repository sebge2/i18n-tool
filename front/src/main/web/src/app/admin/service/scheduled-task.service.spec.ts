import { TestBed } from '@angular/core/testing';

import { ScheduledTaskService } from './scheduled-task.service';

describe('ScheduledTaskService', () => {
  let service: ScheduledTaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScheduledTaskService);
  });

  xit('should be created', () => {
    expect(service).toBeTruthy();
  });
});
