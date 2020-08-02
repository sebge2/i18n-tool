import {AfterContentInit, Component, ContentChildren, OnInit, QueryList} from '@angular/core';
import {TabComponent} from "./tab/tab.component";

@Component({
    selector: 'app-tabs',
    templateUrl: './tabs.component.html',
    styleUrls: ['./tabs.component.scss']
})
export class TabsComponent implements OnInit, AfterContentInit {

    @ContentChildren(TabComponent) public tabComponents: QueryList<TabComponent>;

    public tabs: TabComponent[] = [];

    constructor() {
    }

    ngOnInit() {
    }

    public ngAfterContentInit(): void {
        this.tabs = this.tabComponents.toArray();
    }

}
