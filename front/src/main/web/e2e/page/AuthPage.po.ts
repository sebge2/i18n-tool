import {browser, by, element} from 'protractor';
import {AppPage} from "./AppPage.po";
import {ExpectedConditions} from 'protractor';

export class AuthPage {

    constructor(private _appPage: AppPage) {
    }

    get app(): AppPage {
        return this._appPage;
    }

    async loginWithAuthKeyIfNeeded(): Promise<AppPage> {
        // TODO check username
        const loggedIn = await this.isLoggedIn();

        if (!loggedIn) {
            await this.loginWithAuthKey();
        }

        return this.app;
    }

    async loginWithAdminIfNeeded(): Promise<AppPage> {
        // TODO check username
        const loggedIn = await this.isLoggedIn();

        if (!loggedIn) {
            await this.loginWithAdmin();
        }

        return this.app;
    }

    async logout(): Promise<AuthPage> {
        await element(by.id('currentUserButtonAction')).click();
        await browser.wait(ExpectedConditions.visibilityOf(element(by.id('logoutLink'))), 5000);
        await element(by.id('logoutLink')).click();

        expect(this.isLoggedIn()).toBeFalsy();

        await element(by.id('backToHomepage')).click();

        return this;
    }

    async isLoggedIn(): Promise<Boolean> {
        const currentUrl = await browser.getCurrentUrl();

        if (!currentUrl.startsWith(browser.baseUrl)) {
            await this.app.browser.openApp();
        }

        return (await element(by.id('currentUserName')).isPresent());
    }

    async loginWithAuthKey(authKey?: string): Promise<AppPage> {
        await element(by.id('authKey')).sendKeys(authKey ? authKey : process.env.E2E_GIT_HUB_AUTH_TOKEN);
        await element(by.id('authKeyLogin')).click();

        const currentRouteAfterLogin = await this.app.browser.currentRoute();

        if (currentRouteAfterLogin != '/translations') {
            console.error('Browser logs', await this.app.browser.console.getBrowserLogs());

            throw new Error('Error while trying to login. Current route ' + currentRouteAfterLogin + '.');
        }

        return this.app;
    }

    async loginWithAdmin(): Promise<AppPage> {
        return this.loginWithUser(
            'admin',
            process.env.E2E_DEFAULT_ADMIN_PASSWORD ? process.env.E2E_DEFAULT_ADMIN_PASSWORD : 'adminPassword'
        );
    }

    async loginWithUser(username: string, password: string): Promise<AppPage> {
        await browser.waitForAngularEnabled(false);

        await element(by.id('username')).sendKeys(username);
        await element(by.id('password')).sendKeys(password);
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