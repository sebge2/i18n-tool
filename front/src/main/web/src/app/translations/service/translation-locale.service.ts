import {Injectable} from '@angular/core';
import {TranslationLocale} from "../model/translation-locale.model";
import {TranslationLocaleService as ApiTranslationLocaleService} from "../../api";
import {NotificationService} from "../../core/notification/service/notification.service";
import {Observable} from "rxjs";
import {synchronizedCollection} from "../../core/shared/utils/synchronized-observable-utils";
import {Events} from "../../core/event/model/events.model";
import {catchError} from "rxjs/operators";
import {EventService} from "../../core/event/service/event.service";

@Injectable({
    providedIn: 'root'
})
export class TranslationLocaleService {

    private readonly _availableLocales: Observable<TranslationLocale[]>;

    constructor(private apiService: ApiTranslationLocaleService,
                private eventService: EventService,
                private notificationService: NotificationService) {
        this._availableLocales = synchronizedCollection(
            this.apiService.findAll1(),
            this.eventService.subscribeDto(Events.ADDED_TRANSLATION_LOCALE),
            this.eventService.subscribeDto(Events.UPDATED_TRANSLATION_LOCALE),
            this.eventService.subscribeDto(Events.DELETED_TRANSLATION_LOCALE),
            dto => new TranslationLocale(dto),
            (first, second) => first.id === second.id
        )
            .pipe(catchError((reason) => {
                console.error("Error while retrieving locales.", reason);
                this.notificationService.displayErrorMessage("Error while retrieving available locales.");
                return [];
            }));
    }

    getAvailableLocales(): Observable<TranslationLocale[]> {
        return this._availableLocales;
    }
}
