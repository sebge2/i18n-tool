var HtmlScreenshotReporter = require('protractor-jasmine2-screenshot-reporter');

var screenshotReporter = new HtmlScreenshotReporter({
    dest: process.cwd() + '/../../../../target/screenshots',
    filename: 'e2e-test-report.html'
});

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
        jasmine.getEnv().addReporter(screenshotReporter);
    },

    afterLaunch: function(exitCode) {
        return new Promise(function(resolve){
            screenshotReporter.afterLaunch(resolve.bind(this, exitCode));
        });
    }
};