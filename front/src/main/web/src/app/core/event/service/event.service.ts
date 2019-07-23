import {Injectable} from '@angular/core';
import {RxStompService} from '@stomp/ng2-stompjs';
import {Message} from '@stomp/stompjs';
import {Observable} from "rxjs";
import {map} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class EventService {

    constructor(private rxStompService: RxStompService) {
    }

    public subscribe<T>(eventType: string, type: { new(raw: T): T; }): Observable<T> {
        return this.rxStompService.watch("/topic/" + eventType)
            .pipe(map((message: Message) => new type(<T>JSON.parse(message.body))));
    }

    public publish(eventType: string, payload: any): void {
        this.rxStompService.publish({destination: "/app/" + eventType, body: JSON.stringify(payload)});
    }
}
