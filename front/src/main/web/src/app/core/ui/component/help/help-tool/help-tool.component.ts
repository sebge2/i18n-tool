import {Component} from '@angular/core';

@Component({
    selector: 'app-help-tool',
    template: `
    <a href="https://github.com/sebge2/i18n-tool" target="_blank">
        {{'SHARED.HELP.TOOL.LINK_LABEL' | translate}}
    </a>`,
})
export class HelpToolComponent {

    constructor() {
    }

}
