import {
    AfterContentInit,
    Component,
    ContentChildren,
    EventEmitter,
    OnDestroy, Output,
    QueryList,
    ViewChild
} from '@angular/core';
import {TabComponent} from "./tab/tab.component";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {MatTabGroup} from "@angular/material/tabs";

@Component({
    selector: 'app-tabs',
    templateUrl: './tabs.component.html',
    styleUrls: ['./tabs.component.scss']
})
export class TabsComponent implements AfterContentInit, OnDestroy {

    @Output() public selectedTab = new EventEmitter<number>();
    @ContentChildren(TabComponent) public tabComponents: QueryList<TabComponent>;
    @ViewChild('tabGroup', {static: false}) private tabGroup: MatTabGroup;

    private _destroyed$ = new Subject<void>();

    public tabs: TabComponent[] = [];

    constructor() {
    }

    public ngAfterContentInit(): void {
        this.tabComponents
            .changes
            .pipe(takeUntil(this._destroyed$))
            .subscribe(() => this.tabs = this.tabComponents.toArray());

        setTimeout(() => {
            this.tabs = this.tabComponents.toArray();
        });
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public selectTab(tabIndex: number){
        this.tabGroup.selectedIndex = tabIndex;
    }

    public onSelectedTab(index: number) {
        this.selectedTab.emit(index);
    }
}
