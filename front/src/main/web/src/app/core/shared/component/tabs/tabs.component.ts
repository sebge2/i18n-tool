import {AfterContentInit, Component, ContentChildren, QueryList} from '@angular/core';
import {TabComponent} from "./tab/tab.component";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

@Component({
    selector: 'app-tabs',
    templateUrl: './tabs.component.html',
    styleUrls: ['./tabs.component.scss']
})
export class TabsComponent implements AfterContentInit {

    @ContentChildren(TabComponent) public tabComponents: QueryList<TabComponent>;

    private _destroyed$ = new Subject<void>();

    public tabs: TabComponent[] = [];

    constructor() {
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public ngAfterContentInit(): void {
        this.tabComponents
            .changes
            .pipe(takeUntil(this._destroyed$))
            .subscribe(() => {
                this.tabs = this.tabComponents.toArray();
            })

        setTimeout(() => {
            this.tabs = this.tabComponents.toArray();
        });
    }

}
