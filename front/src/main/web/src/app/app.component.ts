import {Component} from '@angular/core';
import {LocaleService} from "./core/ui/service/locale.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {

    constructor(private localeService: LocaleService) {
        localeService.initializeTranslationService();
    }
}
