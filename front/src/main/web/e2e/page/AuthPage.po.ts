import {browser, by, element} from 'protractor';
import {AppPage} from "./AppPage.po";

export class AuthPage {

    constructor(private _appPage: AppPage) {
    }

    appPage(): AppPage {
        return this._appPage;
    }

    async loginWithAuthKeyIfNeeded(): Promise<AppPage> {
        return browser.getCurrentUrl()
            .then(async (currentUrl: string) => {
                if (!currentUrl.startsWith(browser.baseUrl)) {
                    await this.appPage().browserPage().openApp();
                }

                let route = await this.appPage().browserPage().getCurrentRoute();

                if (route !== '/login') {
                    return this.appPage();
                }

                return element(by.id('authKey'))
                    .sendKeys(process.env.E2E_GIT_HUB_AUTH_TOKEN)
                    .then(() => element(by.id('authKeyLogin')).click())
                    .then(async () => {
                        const currentRouteAfterLogin = await this.appPage().browserPage().getCurrentRoute();

                        const logs = await this.appPage().browserPage().consolePage().getBrowserLogs();// TODO
                        if(currentRouteAfterLogin != '/translations'){
                            console.error(logs); // TODO
                            throw new Error('Error while trying to login. Current route ' + currentRouteAfterLogin + '.');
                        }

                        return this.appPage();
                    });
            });
    }
}