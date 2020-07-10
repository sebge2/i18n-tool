import {Injectable, NgZone} from '@angular/core';
import {EMPTY, Observable} from "rxjs";
import {NotificationService} from "../../notification/service/notification.service";
import {EventObjectDto} from "../../../api";
import {catchError, filter, map} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class EventService {

    private _observable: Observable<EventObjectDto>;

    constructor(private _zone: NgZone,
                private notificationService: NotificationService) {
        const eventSource = new EventSource("./api/event", {withCredentials: true});

        this._observable = new Observable(observer => {
            eventSource.addEventListener('message', function (e) {
                _zone.run(() => {
                    observer.next(<EventObjectDto>JSON.parse(e.data));
                });
            }, false);

            eventSource.addEventListener('error', function (e) {
                _zone.run(() => observer.error(e));
            }, false);
        });
    }

    public subscribe<T>(eventType: string, type: { new(raw: T): T; }): Observable<T> {
        return this._observable
            .pipe(
                catchError((e) => {
                    // TODO display once
                    this.notificationService.displayErrorMessage('Connection issue to the server. Please check your internet connection.');

                    return EMPTY;
                }),
                filter(event => !!event),
                filter(event => event.type === eventType),
                map(event => new type(<T>JSON.parse(JSON.stringify(event.payload)))),
            );
    }
}
