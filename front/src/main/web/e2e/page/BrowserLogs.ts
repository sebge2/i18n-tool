import {logging} from "selenium-webdriver";

export class BrowserLogs {

    constructor(private logs: logging.Entry[]) {
    }

    keepLevels(...levels: logging.Level[]): BrowserLogs {
        return new BrowserLogs(this.logs.filter(entry => levels.indexOf(entry.level) >= 0));
    }

    removeMessages(messagePattern: string): BrowserLogs {
        return new BrowserLogs(this.logs.filter(entry => !entry.message.includes(messagePattern)));
    }

    assertNoLog() {
        if (this.logs.length > 0) {
            throw new Error("There are logs: [" +  this.logs.map(log => "\n" + log.message) + "\n].");
        }
    }
}