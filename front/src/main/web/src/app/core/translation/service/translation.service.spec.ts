import { getTestBed, TestBed } from '@angular/core/testing';

import { TranslationService } from './translation.service';
import { NotificationService } from '@i18n-core-notification';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CoreSharedModule } from '@i18n-core-shared';

describe('TranslationsService', () => {
  let injector: TestBed;
  let service: TranslationService;
  let notificationService: NotificationService;

  beforeEach(() => {
    notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CoreSharedModule],
    });

    injector = getTestBed();
    service = injector.get(TranslationService);
  });

  xit('should be created', () => {
    expect(service).toBeTruthy();
  });
});
