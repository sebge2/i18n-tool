import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-more-actions-button',
    templateUrl: './more-actions-button.component.html',
})
export class MoreActionsButtonComponent {

    @Input() public disabled: boolean = false;
    @Input() public actionInProgress: boolean = false;

    constructor() {
    }

}
