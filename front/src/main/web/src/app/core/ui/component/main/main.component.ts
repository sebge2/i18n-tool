import {Component, ViewChild} from '@angular/core';
import {MenuContainerComponent} from '@i18n-core-shared';

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
})
export class MainComponent {

    @ViewChild('sideBar', { static: false }) sideBar: MenuContainerComponent;

    onMenuToggle() {
        this.sideBar.toggle();
    }
}
