import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class PreferencesService {
  constructor() {}

  public getPreference(key: string): Observable<any> {
    return of(localStorage.getItem(key)).pipe(map((val) => JSON.parse(val)));
  }

  public setPreference(key: string, value: any): Observable<any> {
    return of(value).pipe(
      map((val) => JSON.stringify(val)),
      tap((val) => localStorage.setItem(key, val))
    );
  }
}
