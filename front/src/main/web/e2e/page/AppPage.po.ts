import {BrowserPage} from "./BrowserPage.po";
import {AuthPage} from "./AuthPage.po";
import {MenuPage} from "./MenuPage.po";

export class AppPage {

    constructor(private _browserPage: BrowserPage) {
    }

    browserPage(): BrowserPage {
        return this._browserPage;
    }

    authPage(): AuthPage {
        return new AuthPage(this);
    }

    menuPage(): MenuPage {
        return new MenuPage(this);
    }
}