exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    baseURL: 'http://localhost:8080/',
    framework: 'jasmine',

    specs: ['**/*.spec.js'],

    suites: {
        smoke: ['smoke-tests/*.spec.js'],
        complete: ['smoke-tests/*.spec.js', 'other-tests/*.spec.js']
    },

    capabilities: {
        browserName: 'chrome',
        chromeOptions: {
            args: ['--headless', '--disable-gpu', '--no-sandbox', '--disable-extensions', '--disable-dev-shm-usage']
        }
    }
};