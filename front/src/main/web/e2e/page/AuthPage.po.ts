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

                const gitHubAuthToken = process.env.GIT_HUB_AUTH_TOKEN;

                return element(by.id('authKey'))
                    .sendKeys(gitHubAuthToken)
                    .then(() => element(by.id('authKeyLogin')).click())
                    .then(async () => {
                        const currentRouteAfterLogin = await this.appPage().browserPage().getCurrentRoute();

                        expect(currentRouteAfterLogin).toBe('/translations');

                        return this.appPage();
                    });
            });
    }
}