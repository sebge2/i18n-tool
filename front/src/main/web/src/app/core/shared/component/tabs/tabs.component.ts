import {
  AfterContentInit,
  Component,
  ContentChildren,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  QueryList,
  ViewChild,
} from '@angular/core';
import { TabComponent } from './tab/tab.component';
import { Subject } from 'rxjs';
import { take, takeUntil } from 'rxjs/operators';
import { MatTabGroup } from '@angular/material/tabs';
import { ActivatedRoute, Router } from '@angular/router';
import * as _ from 'lodash';

@Component({
  selector: 'app-tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.scss'],
})
export class TabsComponent implements OnInit, AfterContentInit, OnDestroy {
  @Input() public enableInitialTab: boolean = false;
  @Input() public tabUrlParam: string = 'tab';
  @Output() public selectedTab = new EventEmitter<number>();
  @Output() public initialTab = new EventEmitter<string>();

  @ContentChildren(TabComponent) public tabComponents: QueryList<TabComponent>;

  public tabs: TabComponent[] = [];

  @ViewChild('tabGroup', { static: false }) private tabGroup: MatTabGroup;
  private _initialTabId: string;
  private _destroyed$ = new Subject<void>();

  constructor(private _activatedRoute: ActivatedRoute, private _router: Router) {}

  public ngOnInit(): void {
    this._activatedRoute.queryParamMap
      .pipe(takeUntil(this._destroyed$), take(1))
      .toPromise()
      .then((queryParams) => {
        if (this.enableInitialTab) {
          this._initialTabId = queryParams.get(this.tabUrlParam);
          this.initialTab.emit(this._initialTabId);
        }
      });
  }

  public ngAfterContentInit(): void {
    this.tabComponents.changes.pipe(takeUntil(this._destroyed$)).subscribe(() => this.updateTabs());

    setTimeout(() => this.updateTabs());
  }

  public ngOnDestroy(): void {
    this._destroyed$.next(null);
    this._destroyed$.complete();
  }

  public selectTab(tabIndex: number) {
    this.tabGroup.selectedIndex = tabIndex;
  }

  public onSelectedTab(index: number) {
    this.selectedTab.emit(index);

    if (this.enableInitialTab) {
      const updatedParams = {};
      updatedParams[this.tabUrlParam] = this.tabs[index].id;

      this._router.navigate([], {
        relativeTo: this._activatedRoute,
        queryParams: updatedParams,
        queryParamsHandling: 'merge',
      });
    }
  }

  private updateTabs() {
    this.tabs = this.tabComponents.toArray();
    this.selectInitialTab();
  }

  private selectInitialTab() {
    if (this._initialTabId) {
      const index = _.findIndex(this.tabs, (tab) => _.isEqual(tab.id, this._initialTabId));
      if (index >= 0) {
        this.selectTab(index);
      }

      this._initialTabId = null;
    }
  }
}
