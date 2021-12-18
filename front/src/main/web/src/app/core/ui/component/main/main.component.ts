import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ScreenService } from '../../service/screen.service';
import { Subscription } from 'rxjs';
import { MatSidenav } from '@angular/material';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
})
export class MainComponent implements OnInit, OnDestroy {
  @ViewChild('snav', { static: false }) sideNav: MatSidenav;

  private _smallSizeSubscription: Subscription;
  private _smallSize: boolean;

  constructor(private mediaService: ScreenService) {}

  ngOnInit(): void {
    this._smallSizeSubscription = this.mediaService.smallSize.subscribe((mobileSize) => (this._smallSize = mobileSize));
  }

  ngOnDestroy(): void {
    this._smallSizeSubscription.unsubscribe();
  }

  get smallSize(): boolean {
    return this._smallSize;
  }

  onClick() {
    if (this.smallSize) {
      this.sideNav.close();
    }
  }
}
