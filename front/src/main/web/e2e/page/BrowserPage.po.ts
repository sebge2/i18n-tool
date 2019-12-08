import {browser} from 'protractor';
import {ConsolePage} from "./ConsolePage.po";
import {AppPage} from "./AppPage.po";

export class BrowserPage {

    constructor() {
    }

    get console(): ConsolePage {
        return new ConsolePage(this);
    }

    openApp(): Promise<AppPage> {
        return this.openAppRoute('');
    }

    get currentRoute() {
        return (async () => {
            const currentUrl = await browser.getCurrentUrl();

            if (currentUrl.startsWith(browser.baseUrl)) {
                const route = currentUrl.substring(browser.baseUrl.length);

                return route.startsWith("/")
                    ? route
                    : "/" + route;
            } else {
                throw new Error("The URL [" + currentUrl + "] does not start with [" + browser.baseUrl + "].");
            }
        });
    };

    async openAppRoute(route: string): Promise<AppPage> {
        await browser.get(browser.baseUrl + route);

        return new AppPage(this);
    }

}