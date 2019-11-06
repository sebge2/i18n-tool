import {browser} from 'protractor';
import {ConsolePage} from "./ConsolePage.po";
import {AppPage} from "./AppPage.po";

export class BrowserPage {

    constructor() {
    }

    openApp(): Promise<AppPage> {
        return this.openAppRoute('');
    }

    async openAppRoute(route: string): Promise<AppPage> {
        return browser.get(browser.baseUrl + route).then(() => new AppPage(this));
    }

    async getCurrentRoute(): Promise<string> {
        return browser.getCurrentUrl()
            .then((currentUrl: string) => {
                    if (currentUrl.startsWith(browser.baseUrl)) {
                        const route = currentUrl.substring(browser.baseUrl.length);

                        return route.startsWith("/")
                            ? route
                            : "/" + route;
                    } else {
                        throw new Error("The URL [" + currentUrl + "] does not start with [" + browser.baseUrl + "].");
                    }
                }
            );
    };

    consolePage(): ConsolePage {
        return new ConsolePage(this);
    }

}