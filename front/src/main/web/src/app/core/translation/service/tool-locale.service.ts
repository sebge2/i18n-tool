import { Injectable } from '@angular/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { ALL_LOCALES, DEFAULT_LOCALE, ToolLocale } from '../model/tool-locale.model';
import { TranslateService } from '@ngx-translate/core';
import { UserPreferencesService } from './user-preferences.service';
import { distinctUntilChanged, flatMap, map, shareReplay, skip } from 'rxjs/operators';
import { ActivatedRoute, Params } from '@angular/router';
import { Locale } from '../model/locale.model';

@Injectable({
  providedIn: 'root',
})
export class ToolLocaleService {
  public static FORCE_LOCALE = 'forceLocale';

  private readonly _currentLocale$ = new BehaviorSubject<ToolLocale>(null);
  private readonly _currentLocaleObs$ = this._currentLocale$.pipe(skip(1), shareReplay(1));
  private readonly _forceLocale$: Observable<ToolLocale>;
  private readonly _toolLocale$: Observable<ToolLocale>;
  private readonly _browserLocalePreference$ = new BehaviorSubject(this.getLocaleFromBrowserPreference());

  constructor(
    private translateService: TranslateService,
    private preferencesServices: UserPreferencesService,
    private route: ActivatedRoute
  ) {
    this.translateService.setDefaultLang(DEFAULT_LOCALE.toString());
    this.translateService.addLangs(ALL_LOCALES.map((locale) => locale.toString()));

    this._forceLocale$ = this.route.queryParamMap.pipe(
      map((params: Params) => this.findLocaleFromString(params.get(ToolLocaleService.FORCE_LOCALE)))
    );

    this._toolLocale$ = this.preferencesServices.getUserPreferences().pipe(
      map((preferences) => (preferences != null ? preferences.toolLocale : null)),
      distinctUntilChanged()
    );

    combineLatest([this.getPreferredToolLocale(), this._forceLocale$, this._browserLocalePreference$])
      .pipe(
        map(([userPreferredLocale, forceLocale, browserLocalePreference]) => {
          if (forceLocale != null) {
            return forceLocale;
          } else if (userPreferredLocale != null) {
            return userPreferredLocale;
          } else if (browserLocalePreference != null) {
            return browserLocalePreference;
          } else {
            return DEFAULT_LOCALE;
          }
        }),
        flatMap((locale) => this.translateService.use(locale.toString()).pipe(map((_) => locale)))
      )
      .subscribe((locale) => this._currentLocale$.next(locale));
  }

  getAvailableToolLocales(): ToolLocale[] {
    return ALL_LOCALES;
  }

  getCurrentLocale(): Observable<ToolLocale> {
    return this._currentLocaleObs$;
  }

  getPreferredToolLocale(): Observable<ToolLocale> {
    return this._toolLocale$;
  }

  private findLocaleFromString(value: string): ToolLocale {
    return this.getAvailableToolLocales().find((locale) => locale.toLocale().matchStrictly(Locale.fromString(value)));
  }

  private getLocaleFromBrowserPreference(): ToolLocale {
    for (const browserLanguage of navigator.languages) {
      for (const locale of this.getAvailableToolLocales()) {
        if (locale.toLocale().matchLanguage(Locale.fromString(browserLanguage))) {
          return locale;
        }
      }
    }

    return null;
  }
}
