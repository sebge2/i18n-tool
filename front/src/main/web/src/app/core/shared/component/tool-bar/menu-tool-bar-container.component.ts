import {Component, ViewChild} from '@angular/core';
import {ToolBarService} from "@i18n-core-shared";
import {MatSidenav} from "@angular/material/sidenav";

@Component({
    selector: 'app-menu-tool-bar-container',
    templateUrl: './menu-tool-bar-container.component.html',
    styleUrls: ['./menu-tool-bar-container.component.scss']
})
export class MenuToolBarContainerComponent {

    @ViewChild('snav', {static: false}) sideNav: MatSidenav;

    constructor(public toolBarService: ToolBarService) {
    }
}
