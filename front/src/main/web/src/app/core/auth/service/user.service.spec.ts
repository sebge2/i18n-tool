import {getTestBed, TestBed} from '@angular/core/testing';

import {UserService} from './user.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {EventService} from "../../event/service/event.service";
import {NotificationService} from "../../notification/service/notification.service";
import {User} from "../model/user.model";
import {UserDto} from "../../../api";

describe('UserService', () => {
    let injector: TestBed;
    let service: UserService;
    let eventService: EventService;
    let notificationService: NotificationService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);
        eventService = jasmine.createSpyObj('eventService', ['subscribe']);

        TestBed.configureTestingModule({
            imports: [
                HttpClientTestingModule
            ],
            providers: [
                UserService,
                {provide: NotificationService, useValue: notificationService},
                {provide: EventService, useValue: eventService}
            ]
        });

        injector = getTestBed();
        service = injector.get(UserService);
        httpMock = injector.get(HttpTestingController);
    });

    xit('should get users',
        async () => {
            const expected = [User.fromDto(<UserDto>{id: 'id'})];

            const promise = service.getUsers()
                .toPromise()
                .then((actual: User[]) => {
                    expect(actual).toEqual(expected);
                });
// TODO
            httpMock.expectOne('/api/user').flush(expected);
            httpMock.verify();

            return promise;
        }
    );
});
