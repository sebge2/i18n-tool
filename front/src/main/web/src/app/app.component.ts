import {Component} from '@angular/core';
import {ToolLocaleService} from "./core/ui/service/tool-locale.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {

    constructor(private localeService: ToolLocaleService) {
        localeService.initialize();
    }
}
