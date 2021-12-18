import { Component, OnDestroy, OnInit } from '@angular/core';
import { TranslationLocaleService } from '@i18n-core-translation';
import { TranslationLocale } from '@i18n-core-translation';
import { BehaviorSubject, combineLatest, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import * as _ from 'lodash';

@Component({
  selector: 'app-locales',
  templateUrl: './locales.component.html',
  styleUrls: ['./locales.component.css'],
})
export class LocalesComponent implements OnInit, OnDestroy {
  locales: TranslationLocale[] = [];

  private readonly _addedLocales = new BehaviorSubject<TranslationLocale[]>([]);
  private readonly _destroyed$ = new Subject<void>();

  constructor(public translationLocaleService: TranslationLocaleService) {}

  ngOnInit() {
    combineLatest([this.translationLocaleService.getAvailableLocales(), this._addedLocales])
      .pipe(takeUntil(this._destroyed$))
      .subscribe(([availableLocales, addedLocales]) => {
        this.locales = [];
        this.locales = _.concat(this.locales, availableLocales);
        this.locales = _.concat(this.locales, addedLocales);
      });
  }

  ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  onAdd() {
    const locales = this._addedLocales.getValue();
    locales.push(TranslationLocale.create());

    this._addedLocales.next(locales);
  }

  onSave(locale: TranslationLocale) {
    this._removeFromAddedLocales(locale);
  }

  onDelete(locale: TranslationLocale) {
    this._removeFromAddedLocales(locale);
  }

  private _removeFromAddedLocales(locale: TranslationLocale) {
    const locales = this._addedLocales.getValue();
    const indexOf = locales.indexOf(locale);

    if (indexOf >= 0) {
      locales.splice(indexOf, 1);
      this._addedLocales.next(locales);
    }
  }
}
