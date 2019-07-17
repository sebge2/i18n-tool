import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {InjectableRxStompConfig, RxStompService, rxStompServiceFactory} from '@stomp/ng2-stompjs';

export const myRxStompConfig: InjectableRxStompConfig = {
    brokerURL: 'ws://' + window.location.host + '/ws/websocket'
};

@NgModule({
    declarations: [],
    imports: [
        CommonModule
    ],
    providers: [
        {
            provide: InjectableRxStompConfig,
            useValue: myRxStompConfig
        },
        {
            provide: RxStompService,
            useFactory: rxStompServiceFactory,
            deps: [InjectableRxStompConfig]
        }
    ],
    exports: []
})
export class CoreEventModule {
}
