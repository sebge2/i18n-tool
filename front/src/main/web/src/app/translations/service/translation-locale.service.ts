import {Injectable, OnDestroy} from '@angular/core';
import {TranslationLocale} from "../model/translation-locale.model";
import {TranslationLocaleService as ApiTranslationLocaleService} from "../../api";
import {NotificationService} from "../../core/notification/service/notification.service";
import {BehaviorSubject, Subject} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class TranslationLocaleService implements OnDestroy {

    private _availableLocales: BehaviorSubject<TranslationLocale[]> = new BehaviorSubject<TranslationLocale[]>([]);

    private destroy$ = new Subject();

    constructor(private apiService: ApiTranslationLocaleService,
                private notificationService: NotificationService) {
        this.apiService
            .findAll1()
            .toPromise()
            .then(locales => this._availableLocales.next(locales.map(locale => new TranslationLocale(locale))))
            .catch(reason => {
                console.error("Error while retrieving locales.", reason);
                this.notificationService.displayErrorMessage("Error while retrieving available locales.");
            });
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    getAvailableLocales(): TranslationLocale[] {
        return this._availableLocales.getValue();
    }
}
