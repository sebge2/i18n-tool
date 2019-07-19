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

    public subscribe<T>(destination: string, type: { new(raw: T): T; }): Observable<T> {
        return this.rxStompService.watch("/topic/" + destination)
            .pipe(map((message: Message) => new type(<T>JSON.parse(message.body))));
    }

    public publish(destination: string, payload: any): void {
        this.rxStompService.publish({destination: "/app/" + destination, body: JSON.stringify(payload)});
    }
}
