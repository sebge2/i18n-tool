import { getTestBed, TestBed } from '@angular/core/testing';

import { AuthenticationService } from './authentication.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NotificationService } from '@i18n-core-notification';
import { EventService } from '@i18n-core-event';
import { AuthenticatedUser } from '@i18n-core-auth';

describe('AuthenticationService', () => {
  let injector: TestBed;
  let service: AuthenticationService;
  let eventService: EventService;
  let notificationService: NotificationService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);
    eventService = jasmine.createSpyObj('eventService', ['subscribe']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthenticationService,
        { provide: NotificationService, useValue: notificationService },
        { provide: EventService, useValue: eventService },
      ],
    });

    injector = getTestBed();
    service = injector.get(AuthenticationService);
    httpMock = injector.get(HttpTestingController);
  });

  xit('should get current user', async () => {
    const expected = new AuthenticatedUser();

    const promise = service
      .currentAuthenticatedUser()
      .toPromise()
      .then((actual: AuthenticatedUser) => {
        expect(actual).toEqual(expected);
      });

    httpMock.expectOne('/api/authentication/user').flush(expected);
    httpMock.verify();

    return promise;
  });
});
