import {Component, OnDestroy, OnInit} from '@angular/core';

@Component({
    selector: 'app-menu',
    templateUrl: './menu.component.html',
    styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit, OnDestroy {

    public showAdmin: boolean = true;

    constructor() {
    }

    ngOnInit() {
    }

    ngOnDestroy(): void {
    }

    toggleAdmin() {
        this.showAdmin = !this.showAdmin;
    }
}
