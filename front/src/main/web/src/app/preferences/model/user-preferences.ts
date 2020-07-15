import {UserPreferencesDto} from "../../api";
import {ALL_LOCALES, DEFAULT_LOCALE, ToolLocale} from "../../core/translation/model/tool-locale.model";

export class UserPreferences {

    constructor(private dto: UserPreferencesDto) {
    }

    get toolLocale(): ToolLocale {
        const toolLocale = ALL_LOCALES.find(toolLocale => toolLocale.dtoEnum === this.dto.toolLocale);

        if (toolLocale) {
            return toolLocale;
        }

        return DEFAULT_LOCALE;
    }

    get preferredLocales(): string[] {
        return this.dto.preferredLocales;
    }
}
