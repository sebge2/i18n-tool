import {Component} from '@angular/core';
import {AppVersionService} from "../../../../shared/service/app-version.service";

@Component({
    selector: 'app-help-tool',
    templateUrl: 'help-tool.component.html',
})
export class HelpToolComponent {

    constructor(public appVersionService: AppVersionService) {
    }

    get url(): string {
        return `https://github.com/sebge2/i18n-tool/tree/${this.appVersionService.version}`;
    }

}
