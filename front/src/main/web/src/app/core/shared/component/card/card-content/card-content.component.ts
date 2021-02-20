import {Component} from '@angular/core';

@Component({
    selector: 'app-card-content',
    template: '<ng-content></ng-content>',
})
export class CardContentComponent {

    constructor() {
    }

}
