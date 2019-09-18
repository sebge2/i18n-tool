import {getTestBed, TestBed} from '@angular/core/testing';

import {UserSessionService} from './user-session.service';
import {CoreEventModule} from "../../event/core-event.module";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {Observable, Subject} from "rxjs";
import {Events} from "../../event/model.events.model";
import {EventService} from "../../event/service/event.service";
import {take, toArray} from "rxjs/operators";
import {UserSession} from "../model/user-session.model";

describe('UserSessionService', () => {
    let injector: TestBed;
    let service: UserSessionService;
    let httpMock: HttpTestingController;
    let eventService: MockEventService;

    class MockEventService {

        readonly subject: Subject<UserSession> = new Subject();

        public subscribe(eventType: string, type: any): Observable<UserSession> {
            expect(eventType).toMatch(new RegExp(Events.CONNECTED_USER_SESSION + '|' + Events.DISCONNECTED_USER_SESSION));
            expect(type).toEqual(UserSession);

            return this.subject;
        }
    }

    beforeEach(() => {
        eventService = new MockEventService();

        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule, CoreEventModule],
            providers: [
                UserSessionService,
                {provide: EventService, useValue: eventService}
            ]
        });

        injector = getTestBed();
        service = injector.get(UserSessionService);
        httpMock = injector.get(HttpTestingController);
    });

    it('should get user sessions',
        async () => {
            const expected: UserSession[] = [
                new UserSession(<UserSession>{id: 'fgh'}),
                new UserSession(<UserSession>{id: 'def'}),
                new UserSession(<UserSession>{id: 'abc'})
            ];

            const promise = service.getCurrentUserSessions()
                .pipe(take(2), toArray())
                .toPromise()
                .then((actual: UserSession[][]) => {
                    expect(actual).toEqual([[], expected]);
                });

            httpMock.expectOne('/api/user-session/current').flush(expected);
            httpMock.verify();

            return promise;
        }
    );

    // TODO event connected
    // TODO event disconnected
});
