import {browser, by, element} from 'protractor';
import {AppPage} from "./AppPage.po";

export class AuthPage {

    constructor(private _appPage: AppPage) {
    }

    get app(): AppPage {
        return this._appPage;
    }

    async loginWithAuthKeyIfNeeded(): Promise<AppPage> {
        const loggedIn = await this.isLoggedIn();

        if (!loggedIn) {
            await this.loginWithAuthKey();
        }

        return this.app;
    }

    async loginWithAdminIfNeeded(): Promise<AppPage> {
        const loggedIn = await this.isLoggedIn();

        if (!loggedIn) {
            await this.loginWithAdmin();
        }

        return this.app;
    }

    async logout(): Promise<AuthPage> {
        await element(by.id('currentUserButtonAction')).click();
        await element(by.id('logoutLink')).click();

        return this;
    }

    async isLoggedIn(): Promise<Boolean> {
        const currentUrl = await browser.getCurrentUrl();

        if (!currentUrl.startsWith(browser.baseUrl)) {
            await this.app.browser.openApp();
        }

        return (await element(by.id('currentUserName')).isPresent());
    }

    private async loginWithAuthKey(): Promise<AppPage> {
        await element(by.id('authKey')).sendKeys(process.env.E2E_GIT_HUB_AUTH_TOKEN);
        await element(by.id('authKeyLogin')).click();

        const currentRouteAfterLogin = await this.app.browser.currentRoute();

        if (currentRouteAfterLogin != '/translations') {
            console.error('Browser logs', await this.app.browser.console.getBrowserLogs());

            throw new Error('Error while trying to login. Current route ' + currentRouteAfterLogin + '.');
        }

        return this.app;
    }

    private async loginWithAdmin(): Promise<AppPage> {
        await browser.waitForAngularEnabled(false);

        await element(by.id('username')).sendKeys('admin');
        await element(by.id('password')).sendKeys(process.env.E2E_DEFAULT_ADMIN_PASSWORD ? process.env.E2E_DEFAULT_ADMIN_PASSWORD : 'adminPassword');
        await element(by.id('authUserPasswordLogin')).click();

        await browser.waitForAngularEnabled(true);

        const currentRouteAfterLogin = await this.app.browser.currentRoute();

        if (currentRouteAfterLogin != '/translations') {
            console.error('Browser logs', await this.app.browser.console.getBrowserLogs());

            throw new Error('Error while trying to login. Current route ' + currentRouteAfterLogin + '.');
        }

        return this.app;
    }
}