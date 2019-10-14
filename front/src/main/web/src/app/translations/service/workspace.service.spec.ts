import {getTestBed, TestBed} from '@angular/core/testing';

import {WorkspaceService} from './workspace.service';
import {CoreEventModule} from "../../core/event/core-event.module";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {Observable, Subject} from "rxjs";
import {Repository} from "../model/repository.model";
import {Events} from "../../core/event/model.events.model";
import {EventService} from "../../core/event/service/event.service";
import {take, toArray} from "rxjs/operators";
import {Workspace} from "../model/workspace.model";
import {NotificationService} from "../../core/notification/service/notification.service";

describe('WorkspaceService', () => {
    let injector: TestBed;
    let service: WorkspaceService;
    let httpMock: HttpTestingController;
    let eventService: MockEventService;
    let notificationService: NotificationService;

    class MockEventService {

        readonly subject: Subject<Repository> = new Subject();

        public subscribe(eventType: string, type: any): Observable<Repository> {
            expect(eventType).toMatch(new RegExp(Events.UPDATED_WORKSPACE + '|' + Events.DELETED_WORKSPACE));
            expect(type).toEqual(Workspace);

            return this.subject;
        }
    }

    beforeEach(() => {
        eventService = new MockEventService();
        notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);

        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule, CoreEventModule],
            providers: [
                WorkspaceService,
                {provide: EventService, useValue: eventService},
                {provide: NotificationService, useValue: notificationService}
            ]
        });

        injector = getTestBed();
        service = injector.get(WorkspaceService);
        httpMock = injector.get(HttpTestingController);
    });

    it('should get ordered workspaces',
        async () => {
            const firstExpected: Workspace[] = [];
            const workspaces = [
                new Workspace(<Workspace>{id: 'fgh', branch: 'release/2019.7'}),
                new Workspace(<Workspace>{id: 'def', branch: 'release/2019.6'}),
                new Workspace(<Workspace>{id: 'abc', branch: 'master'})
            ];
            const expected = [workspaces[2], workspaces[1], workspaces[0]];

            const promise = service.getWorkspaces()
                .pipe(take(2), toArray())
                .toPromise()
                .then((actual: Workspace[][]) => {
                    expect(actual).toEqual([firstExpected, expected]);
                });

            httpMock.expectOne('/api/workspace').flush(workspaces);
            httpMock.verify();

            return promise;
        }
    );

    // TODO add workflow
    // TODO delete workflow
    // TODO find
    // TODO initialize
    // TODO start review
    // TODO delete

});
