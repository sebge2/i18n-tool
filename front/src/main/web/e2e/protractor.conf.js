var isHeadless = !process.argv.includes('--no-headless');

exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    baseUrl: 'http://localhost:8080/',
    framework: 'jasmine',

    specs: ['**/*.spec.js'],

    suites: {
        smoke: ['smoke-tests/*.spec.js'],
        complete: ['smoke-tests/*.spec.js', 'other-tests/*.spec.js']
    },

    capabilities: {
        browserName: 'chrome',
        chromeOptions: {
            args: [
                isHeadless && '--headless',
                '--disable-gpu',
                '--no-sandbox',
                '--disable-extensions',
                '--disable-dev-shm-usage'
            ].filter(Boolean)
        }
    },

    onPrepare: async() => {
        browser.waitForAngularEnabled(false);

        await browser.get(browser.baseUrl);
        await element(by.id('login_field')).sendKeys('sebge2-test-user');
        await element(by.id('password')).sendKeys('SngDkVV6X9ML5cNeehWGgaf');
        await element(by.name('commit')).click();

        browser.waitForAngularEnabled(true);
    }
};