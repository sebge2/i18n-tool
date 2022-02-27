import { BrowserPage } from '../page/BrowserPage.po';
import { AppPage } from '../page/AppPage.po';
import { logging } from 'selenium-webdriver';

describe('Smoke Tests Simple User', function () {
  let app: AppPage;

  beforeAll(function () {
    return new BrowserPage()
      .openApp()
      .then((page) => page.auth.loginWithAuthKeyIfNeeded())
      .then((page) => (app = page));
  });

  afterEach(function () {
    return app.browser.console
      .getBrowserLogs()
      .then((logs) =>
        logs
          .keepLevels(logging.Level.SEVERE)
          .removeMessages('/api/authentication/user')
          .removeMessages('/login')
          .removeMessages('/main-es2015.js')
          .removeMessages('/vendor-es2015.js')
          .assertNoLog()
      );
  });

  it('should land on default route', function () {});

  it('should navigate to admin', function () {
    return app.menu.assertHasAdminItem(false);
  });

  it('should navigate to settings', function () {
    return app.menu.clickOnSettingsItem();
  });

  it('should navigate to translations', function () {
    return app.menu.clickOnTranslationsItem();
  });

  it('should navigate then logout', function () {
    return app.auth.logout();
  });
});
