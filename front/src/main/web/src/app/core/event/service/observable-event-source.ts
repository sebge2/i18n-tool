import { BehaviorSubject, EMPTY, Observable, Subject } from 'rxjs';
import { EventObjectDto } from '../../../api';
import { NgZone } from '@angular/core';
import { filter, mergeMap, shareReplay, skip } from 'rxjs/operators';
import * as _ from 'lodash';
import { NotificationService } from '@i18n-core-notification';

export enum RequestType {
  CONNECT,

  DISCONNECT,

  RECONNECT,
}

export enum TechnicalEvent {
  CONNECTION,

  DISCONNECTION,
}

export const ATTEMPTS_BEFORE_NOTIFICATION = 10;
export const MIN_DELAY_BETWEEN_RECONNECT_IN_MS = 1000;
export const MAX_DELAY_BETWEEN_RECONNECT_IN_MS = 5000;

export class ObservableEventSource {
  private readonly _observable$ = new BehaviorSubject<EventObjectDto>(null);
  private readonly _observableObs = this._observable$.pipe(skip(1), shareReplay(1));
  private readonly _reconnected$ = new Subject<void>();

  private _eventSource: EventSource;
  private readonly _request$ = new BehaviorSubject<RequestType>(RequestType.DISCONNECT);
  private _reconnectAttempt: number = 0;

  constructor(
    private _zone: NgZone,
    private notificationService: NotificationService,
    private _url: string,
    private _eventSourceInitDict?: EventSourceInit
  ) {
    this._request$
      .pipe(
        filter((requestType) => !(requestType === RequestType.CONNECT && this._eventSource)),
        mergeMap((requestType: RequestType) => {
          if (this._eventSource) {
            this._eventSource.close();
          }

          if (requestType === RequestType.DISCONNECT) {
            return EMPTY;
          }

          this._eventSource = new EventSource(this._url, this._eventSourceInitDict);

          return new Observable<EventObjectDto | TechnicalEvent>((observer) => {
            this._eventSource.addEventListener(
              'message',
              function (e) {
                _zone.run(() => {
                  const event = <EventObjectDto>JSON.parse(e.data);

                  if (!_.isNil(event)) {
                    observer.next(event);
                  }
                });
              },
              false
            );

            this._eventSource.addEventListener(
              'open',
              function (e) {
                _zone.run(() => {
                  observer.next(TechnicalEvent.CONNECTION);
                });
              },
              false
            );

            this._eventSource.addEventListener(
              'error',
              function (e) {
                _zone.run(() => observer.next(TechnicalEvent.DISCONNECTION));
              },
              false
            );
          });
        })
      )
      .subscribe((event) => {
        if (event === TechnicalEvent.DISCONNECTION) {
          this._reconnectAttempt++;

          if (this._reconnectAttempt === ATTEMPTS_BEFORE_NOTIFICATION) {
            this.notificationService.displayErrorMessage('SHARED.EVENTS.ERROR_DISCONNECTED');
          }

          setTimeout(() => {
            console.log(`Disconnected, let's reconnect, attempt(s) ${this._reconnectAttempt}.`);
            this.reconnect();
          }, ObservableEventSource.randomIntFromInterval(MIN_DELAY_BETWEEN_RECONNECT_IN_MS, MAX_DELAY_BETWEEN_RECONNECT_IN_MS));
        } else if (event === TechnicalEvent.CONNECTION) {
          if (this._reconnectAttempt > 0) {
            console.log(`Connection re-established after ${this._reconnectAttempt} attempt(s).`);
            this._reconnected$.next();
          }

          if (this._reconnectAttempt >= ATTEMPTS_BEFORE_NOTIFICATION) {
            this.notificationService.displayInfoMessage('SHARED.EVENTS.INFO_RECONNECTED');
          }

          this._reconnectAttempt = 0;
        } else {
          this._reconnectAttempt = 0;
          this._observable$.next(event);
        }
      });
  }

  public source(): Observable<EventObjectDto> {
    return this._observableObs;
  }

  public connect() {
    this._request$.next(RequestType.CONNECT);
  }

  public reconnect() {
    this._request$.next(RequestType.RECONNECT);
  }

  public disconnect() {
    this._request$.next(RequestType.DISCONNECT);
  }

  public reconnected(): Observable<void> {
    return this._reconnected$;
  }

  private static randomIntFromInterval(min: number, max: number) {
    return Math.floor(Math.random() * (max - min + 1) + min);
  }
}
