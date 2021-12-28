import {AfterViewInit, Component, OnDestroy, ViewChild} from '@angular/core';
import {ToolBarService} from "@i18n-core-shared";
import {MatSidenav} from "@angular/material/sidenav";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

@Component({
    selector: 'app-menu-tool-bar-container',
    templateUrl: './menu-tool-bar-container.component.html',
    styleUrls: ['./menu-tool-bar-container.component.scss']
})
export class MenuToolBarContainerComponent implements AfterViewInit, OnDestroy {

    @ViewChild('snav', {static: false}) sideNav: MatSidenav;

    private readonly _destroyed$ = new Subject<void>();

    constructor(public toolBarService: ToolBarService) {
    }

    ngAfterViewInit(): void {
        this.sideNav.openedChange
            .pipe(takeUntil(this._destroyed$))
            .subscribe(opened => this.toolBarService.open(opened));
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }
}
