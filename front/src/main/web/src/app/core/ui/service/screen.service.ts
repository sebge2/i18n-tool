import { Injectable } from '@angular/core';
import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ScreenService {
  private _smallSize: Observable<boolean>;

  constructor(breakpointObserver: BreakpointObserver) {
    this._smallSize = breakpointObserver.observe(['(max-width: 600px)']).pipe(
      map((result: BreakpointState) => {
        return result.matches;
      })
    );
  }

  get smallSize(): Observable<boolean> {
    return this._smallSize;
  }
}
