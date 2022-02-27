import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from "@angular/material/sidenav";
import {ScreenService} from "../../../ui/service/screen.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

@Component({
    selector: 'app-menu-container',
    templateUrl: './menu-container.component.html',
    styleUrls: ['./menu-container.component.scss']
})
export class MenuContainerComponent implements OnInit, OnDestroy {

    @ViewChild('snav', {static: false}) sideNav: MatSidenav;

    smallSize: boolean = true;

    private readonly _destroyed$ = new Subject<void>();

    constructor(private mediaService: ScreenService) {
    }

    ngOnInit(): void {
        this.mediaService
            .smallSize
            .pipe(takeUntil(this._destroyed$))
            .subscribe(smallSize => this.smallSize = smallSize);
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    onClick() {
        if (this.smallSize) {
            this.sideNav.close();
        }
    }

    toggle(): void {
        this.sideNav.toggle();
    }
}
