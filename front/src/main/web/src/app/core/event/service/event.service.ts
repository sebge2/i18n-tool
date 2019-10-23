import {Injectable} from '@angular/core';
import {RxStompService} from '@stomp/ng2-stompjs';
import {Message} from '@stomp/stompjs';
import {Observable} from "rxjs";
import {map, merge} from "rxjs/operators";
import {NotificationService} from "../../notification/service/notification.service";

@Injectable({
    providedIn: 'root'
})
export class EventService {

    constructor(private rxStompService: RxStompService,
                private notificationService: NotificationService) {
        rxStompService.stompErrors$.subscribe(error => {
            console.error(error);
            notificationService.displayErrorMessage('Error while executing STOMP command.');
        });

        rxStompService.webSocketErrors$.subscribe(error => {
            console.error(error);
            notificationService.displayErrorMessage('Websocket connection issue. Please check your internet connection.');
        });
    }

    public subscribe<T>(eventType: string, type: { new(raw: T): T; }): Observable<T> {
        return this.rxStompService.watch("/topic/" + eventType)
            .pipe(
                merge(this.rxStompService.watch("/user/queue/" + eventType)),
                map((message: Message) => new type(<T>JSON.parse(message.body)))
            );
    }

    public publish(eventType: string, payload: any): void {
        this.rxStompService.publish({destination: "/app/" + eventType, body: JSON.stringify(payload)});
    }
}
