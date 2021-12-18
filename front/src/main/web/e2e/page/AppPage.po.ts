import { BrowserPage } from './BrowserPage.po';
import { AuthPage } from './AuthPage.po';
import { MenuPage } from './MenuPage.po';

export class AppPage {
  constructor(private _browserPage: BrowserPage) {}

  get browser(): BrowserPage {
    return this._browserPage;
  }

  get auth(): AuthPage {
    return new AuthPage(this);
  }

  get menu(): MenuPage {
    return new MenuPage(this);
  }
}
