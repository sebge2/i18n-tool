import {Component} from '@angular/core';

@Component({
    selector: 'app-menu',
    templateUrl: './menu.component.html',
    styleUrls: ['./menu.component.css'],
})
export class MenuComponent {
    public showAdmin: boolean = true;

    constructor() {
    }

    toggleAdmin() {
        this.showAdmin = !this.showAdmin;
    }
}
