import { getTestBed, TestBed } from '@angular/core/testing';

import { UserSessionService } from './user-session.service';
import { CoreEventModule } from '@i18n-core-event';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Observable, Subject } from 'rxjs';
import { Events } from '@i18n-core-event';
import { EventService } from '@i18n-core-event';
import { take, toArray } from 'rxjs/operators';
import { UserSession } from '../model/user-session.model';
import { NotificationService } from '@i18n-core-notification';

describe('UserSessionService', () => {
  let injector: TestBed;
  let service: UserSessionService;
  let httpMock: HttpTestingController;
  let eventService: MockEventService;
  let notificationService: NotificationService;

  class MockEventService {
    readonly connected: Subject<UserSession> = new Subject();
    readonly disconnected: Subject<UserSession> = new Subject();

    public subscribe(eventType: string, type: any): Observable<UserSession> {
      expect(eventType).toMatch(new RegExp(Events.CONNECTED_USER_SESSION + '|' + Events.DISCONNECTED_USER_SESSION));
      expect(type).toEqual(UserSession);

      if (eventType == Events.CONNECTED_USER_SESSION) {
        return this.connected;
      } else {
        return this.disconnected;
      }
    }
  }

  beforeEach(() => {
    eventService = new MockEventService();
    notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CoreEventModule],
      providers: [
        { provide: EventService, useValue: eventService },
        { provide: NotificationService, useValue: notificationService },
      ],
    });

    injector = getTestBed();
    service = injector.get(UserSessionService);
    httpMock = injector.get(HttpTestingController);
  });

  it('should get user sessions', async () => {
    const expected: UserSession[] = [
      new UserSession(<UserSession>{ id: 'fgh' }),
      new UserSession(<UserSession>{ id: 'def' }),
      new UserSession(<UserSession>{ id: 'abc' }),
    ];

    const promise = service
      .getCurrentUserSessions()
      .pipe(take(2), toArray())
      .toPromise()
      .then((actual: UserSession[][]) => {
        expect(actual).toEqual([[], expected]);
      });

    httpMock.expectOne('/api/user-session/current').flush(expected);
    httpMock.verify();

    return promise;
  });

  it('should get user sessions after connection', async () => {
    const sessions: UserSession[] = [
      new UserSession(<UserSession>{ id: 'ghi' }),
      new UserSession(<UserSession>{ id: 'def' }),
      new UserSession(<UserSession>{ id: 'abc' }),
    ];

    const firstPromise = service
      .getCurrentUserSessions()
      .pipe(take(2), toArray())
      .toPromise()
      .then((actual: UserSession[][]) => {
        expect(actual).toEqual([[], sessions]);
      });

    httpMock.expectOne('/api/user-session/current').flush(sessions);
    httpMock.verify();

    await firstPromise;

    const newConnection = new UserSession(<UserSession>{ id: 'jkl' });

    eventService.connected.next(newConnection);

    return service
      .getCurrentUserSessions()
      .pipe(take(1), toArray())
      .toPromise()
      .then((actual: UserSession[][]) => {
        expect(actual).toEqual([sessions.concat(newConnection)]);
      });
  });

  it('should get user sessions after disconnection', async () => {
    const sessions: UserSession[] = [
      new UserSession(<UserSession>{ id: 'ghi' }),
      new UserSession(<UserSession>{ id: 'def' }),
      new UserSession(<UserSession>{ id: 'abc' }),
    ];

    const firstPromise = service
      .getCurrentUserSessions()
      .pipe(take(2), toArray())
      .toPromise()
      .then((actual: UserSession[][]) => {
        expect(actual).toEqual([[], sessions]);
      });

    httpMock.expectOne('/api/user-session/current').flush(sessions);
    httpMock.verify();

    await firstPromise;

    eventService.disconnected.next(sessions[0]);

    return service
      .getCurrentUserSessions()
      .pipe(take(1), toArray())
      .toPromise()
      .then((actual: UserSession[][]) => {
        const expected = sessions.slice();
        expected.splice(0, 1);

        expect(actual).toEqual([expected]);
      });
  });
});
