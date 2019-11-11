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
                    // .sendKeys(process.env.E2E_GIT_HUB_AUTH_TOKEN)
                    .sendKeys('c4a5b8b154cf6ed389b654ce17f38088a9a04e6e')
                    .then(() => element(by.id('authKeyLogin')).click())
                    .then(async () => {
                        const currentRouteAfterLogin = await this.appPage().browserPage().getCurrentRoute();

                        if (currentRouteAfterLogin != '/translations') {
                            console.error('Browser logs', await this.appPage().browserPage().consolePage().getBrowserLogs());

                            throw new Error('Error while trying to login. Current route ' + currentRouteAfterLogin + '.');
                        }

                        return this.appPage();
                    });
            });
    }
}