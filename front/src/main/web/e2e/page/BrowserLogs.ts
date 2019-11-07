import {logging} from "selenium-webdriver";

export class BrowserLogs {

    constructor(private logs: logging.Entry[]) {
    }

    keepLevels(...levels: logging.Level[]): BrowserLogs {
        return new BrowserLogs(this.logs.filter(entry => levels.indexOf(entry.level) >= 0));
    }

    removeMessage(messagePattern: string): BrowserLogs {
        return new BrowserLogs(this.logs.filter(entry => !entry.message.includes(messagePattern)));
    }

    assertNoLog() {
        expect(this.logs).toBe([]);
    }
}