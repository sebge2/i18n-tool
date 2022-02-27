import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import { NotificationService } from '@i18n-core-notification';
import { EventObjectDto } from '../../../api';
import { filter, map } from 'rxjs/operators';
import { ObservableEventSource } from './observable-event-source';
import * as _ from 'lodash';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private readonly _observableEventSource$: ObservableEventSource;

  constructor(_zone: NgZone, notificationService: NotificationService) {
    this._observableEventSource$ = new ObservableEventSource(_zone, notificationService, './api/event', {
      withCredentials: true,
    });
  }

  subscribeDto<D>(eventType: string): Observable<D> {
    return this._observableEventSource$.source().pipe(
      filter((event: EventObjectDto) => !_.isNil(event)),
      filter((event: EventObjectDto) => event.type === eventType),
      map((event: EventObjectDto) => <D>event.payload)
    );
  }

  subscribe<T>(eventType: string, type: { new (raw: any): T }): Observable<T> {
    return this.subscribeDto(eventType).pipe(map((event) => (event ? new type(<T>event) : null)));
  }

  disableEvents() {
    this._observableEventSource$.disconnect();
  }

  enabledEvents() {
    this._observableEventSource$.connect();
  }

  reconnected(): Observable<void> {
    return this._observableEventSource$.reconnected();
  }
}
