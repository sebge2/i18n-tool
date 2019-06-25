import {browser, logging} from 'protractor';
import {BrowserPage} from "./BrowserPage.po";
import {BrowserLogs} from "./BrowserLogs";

export class ConsolePage {

    constructor(private _homePage: BrowserPage) {
    }

    homePage(): BrowserPage {
        return this._homePage;
    }

    async getBrowserLogs(): Promise<BrowserLogs> {
        return browser.manage()
            .logs()
            .get(logging.Type.BROWSER)
            .then(logs => new BrowserLogs(logs));
    }
}