import { getTestBed, TestBed } from '@angular/core/testing';

import { EventService } from './event.service';
import { CoreEventModule } from '@i18n-core-event';
import { NotificationService } from '@i18n-core-notification';
import { CoreSharedModule } from '@i18n-core-shared';

describe('EventService', () => {
  let injector: TestBed;
  let service: EventService;
  let notificationService: NotificationService;

  beforeEach(() => {
    notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);

    TestBed.configureTestingModule({
      imports: [CoreEventModule, CoreSharedModule],
    });

    injector = getTestBed();
    service = injector.get(EventService);
  });

  xit('should be created', () => {
    expect(service).toBeTruthy(); // TODO issue-125
  });
});
