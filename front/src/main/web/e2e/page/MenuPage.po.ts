import {AppPage} from "./AppPage.po";
import {by, element} from "protractor";

export class MenuPage {

    constructor(private _appPage: AppPage) {
    }

    appPage(): AppPage {
        return this._appPage;
    }

    async assertHasAdminItem(expected: boolean): Promise<MenuPage> {
        expect(element(by.id('menuAdmin')).isPresent()).toBe(expected);
        return this;
    }

    async clickOnTranslationsItem(): Promise<MenuPage> {
        await element(by.id('menuTranslations')).click();
        return this;
    }

    async clickOnSettingsItem(): Promise<MenuPage> {
        await element(by.id('menuSettings')).click();
        return this;
    }

    async clickOnAdminItem(): Promise<MenuPage> {
        await this.assertHasAdminItem(true);
        await element(by.id('menuAdmin')).click();
        return this;
    }

}