import {Injectable, NgZone} from '@angular/core';
import {BehaviorSubject, EMPTY, Observable} from "rxjs";
import {NotificationService} from "../../notification/service/notification.service";
import {EventObjectDto} from "../../../api";
import {catchError, filter, flatMap, map, shareReplay, skip} from "rxjs/operators";
import * as _ from "lodash";

@Injectable({
    providedIn: 'root'
})
export class EventService {

    private readonly _observable$ = new BehaviorSubject<EventObjectDto>(null);
    private readonly _observableObs = this._observable$.pipe(skip(1), shareReplay(1));
    private readonly _enabled$ = new BehaviorSubject<boolean>(false);

    constructor(private _zone: NgZone,
                private notificationService: NotificationService) {
        this._enabled$
            .pipe(flatMap(enabled => {
                if (!enabled) {
                    return EMPTY;
                }

                const eventSource = new EventSource("./api/event", {withCredentials: true});

                return new Observable<EventObjectDto>(observer => {
                    eventSource.addEventListener('message', function (e) {
                        _zone.run(() => {
                            observer.next(<EventObjectDto>JSON.parse(e.data));
                        });
                    }, false);

                    eventSource.addEventListener('error', function (e) {
                        _zone.run(() => observer.error(e));
                    }, false);
                });
            }))
            .pipe(
                catchError((e) => {
                    // TODO display once
                    this.notificationService.displayErrorMessage('Connection issue to the server. Please check your internet connection.');

                    return EMPTY;
                }),
            )
            .subscribe(event => this._observable$.next(event))
    }

    public subscribeDto<D>(eventType: string): Observable<D> {
        return this._observableObs
            .pipe(
                filter((event: EventObjectDto) => !_.isNil(event)),
                filter((event: EventObjectDto) => event.type === eventType),
                map((event: EventObjectDto) => <D>event.payload),
            );
    }

    public subscribe<T>(eventType: string, type: { new(raw: any): T; }): Observable<T> {
        return this.subscribeDto(eventType)
            .pipe(map(event => new type(<T>event)));
    }

    public disableEvents() {
        this._enabled$.next(false);
    }

    public enabledEvents() {
        this._enabled$.next(true);
    }
}
