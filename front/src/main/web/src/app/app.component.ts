import {Component} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {ALL_LOCALES, Locale} from "./translations/model/locale.model";
import {UserSettingsService} from "./settings/service/user-settings.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {

    constructor(private translateService: TranslateService,
                private settingsService: UserSettingsService) {
        translateService.setDefaultLang(Locale.EN.toString());
        translateService.addLangs(ALL_LOCALES.map(locale => locale.toString()));

        settingsService.getUserLocales()
            .subscribe(
                locales => {
                    translateService.use((locales.length > 0) ? locales[0].toString() : Locale.EN.toString());
                }
            );
    }
}
