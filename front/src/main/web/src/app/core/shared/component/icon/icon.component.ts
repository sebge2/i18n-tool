import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-icon',
    template: '<mat-icon>{{icon}}</mat-icon>',
})
export class IconComponent {

    @Input() icon: string;

    constructor() {
    }

}
