import {BrowserPage} from '../page/BrowserPage.po';
import {AppPage} from "../page/AppPage.po";
import {logging} from "selenium-webdriver";

describe('Smoke Tests', function () {

    let appPage: AppPage;

    beforeAll(async function () {
        new BrowserPage().openApp()
            .then(page => page.authPage().loginWithAuthKeyIfNeeded())
            .then(page => appPage = page);
    });

    afterEach(async function () {
        await appPage.browserPage()
            .consolePage()
            .getBrowserLogs()
            .then(logs =>
                logs
                    .keepLevels(logging.Level.SEVERE)
                    .removeMessages("/api/authentication/user")
                    .removeMessages("/login")
                    .removeMessages("/main-es2015.js")
                    .removeMessages("/vendor-es2015.js")
                    .assertNoLog()
            );
    });

    it('should land on default route', function () {
    });

    xit('should navigate to admin', function () {
        appPage.menuPage().clickOnAdminItem();
    });

    it('should navigate to settings', function () {
        appPage.menuPage().clickOnSettingsItem();
    });

    it('should navigate to translations', function () {
        appPage.menuPage().clickOnTranslationsItem();
    });

});