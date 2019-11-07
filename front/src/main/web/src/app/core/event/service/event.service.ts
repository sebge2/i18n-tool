import {Injectable} from '@angular/core';
import {RxStompService} from '@stomp/ng2-stompjs';
import {Message} from '@stomp/stompjs';
import {Observable} from "rxjs";
import {map, merge} from "rxjs/operators";
import {NotificationService} from "../../notification/service/notification.service";
import {RxStompState} from '@stomp/rx-stomp';

@Injectable({
    providedIn: 'root'
})
export class EventService {

    private opened: boolean;

    constructor(private rxStompService: RxStompService,
                private notificationService: NotificationService) {
        rxStompService.connectionState$.subscribe((event: RxStompState) => {
            if (event == RxStompState.CLOSED) {
                if (this.opened) {
                    notificationService.displayErrorMessage('Connection issue to the server. Please check your internet connection.');
                }
                this.opened = false;
            } else if (event == RxStompState.OPEN) {
                this.opened = true;
            }
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
