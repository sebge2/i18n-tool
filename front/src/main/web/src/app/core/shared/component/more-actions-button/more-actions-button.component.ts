import {Component, Input, OnInit} from '@angular/core';

@Component({
    selector: 'app-more-actions-button',
    templateUrl: './more-actions-button.component.html',
})
export class MoreActionsButtonComponent implements OnInit {

    @Input() public disabled: boolean = false;
    @Input() public inProgress: boolean = false;

    constructor() {
    }

    ngOnInit(): void {
    }

}
