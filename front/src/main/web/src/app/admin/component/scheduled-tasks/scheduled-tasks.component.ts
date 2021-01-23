import {Component} from '@angular/core';

@Component({
    selector: 'app-scheduled-tasks',
    templateUrl: './scheduled-tasks.component.html',
})
export class ScheduledTasksComponent {

    private _initialTab: string;

    constructor() {
    }

    public onInitialTab(initialTab: string) {
        this._initialTab = initialTab;
    }
}
