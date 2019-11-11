var HtmlScreenshotReporter = require('protractor-jasmine2-screenshot-reporter');

var screenshotReporter = new HtmlScreenshotReporter({
    dest: process.cwd() + '/../../../target/screenshots',
    filename: 'e2e-test-report.html'
});

exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    // TODO environment variable server port
    baseUrl: 'http://localhost:8080/',
    framework: 'jasmine',

    specs: ['**/*.spec.*'],

    suites: {
        smoke: ['smoke-tests/*.spec.*'],
        complete: ['smoke-tests/*.spec.*', 'other-tests/*.spec.*']
    },

    capabilities: {
        browserName: 'chrome',
        chromeOptions: {
            args: [
                '--headless',
                '--disable-gpu',
                '--no-sandbox',
                '--disable-extensions',
                '--disable-dev-shm-usage'
            ]
        }
    },

    beforeLaunch: function() {
        return new Promise(function(resolve){
            screenshotReporter.beforeLaunch(resolve);
        });
    },

    onPrepare: async() => {
        require('ts-node').register({
            project: require('path').join(__dirname, './tsconfig.json')
        });

        jasmine.getEnv().addReporter(screenshotReporter);
        // TODO
        browser.sleep(60000);
    },

    afterLaunch: function(exitCode) {
        return new Promise(function(resolve){
            screenshotReporter.afterLaunch(resolve.bind(this, exitCode));
        });
    }
};