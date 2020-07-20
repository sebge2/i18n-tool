import {UserPreferencesDto} from "../../api";
import {ALL_LOCALES, DEFAULT_LOCALE, ToolLocale} from "../../core/translation/model/tool-locale.model";

export class UserPreferences {

    public static fromDto(dto: UserPreferencesDto): UserPreferences {
        const toolLocale = ALL_LOCALES.find(toolLocale => toolLocale.toDto() === dto.toolLocale);

        return new UserPreferences(
            toolLocale ? toolLocale : DEFAULT_LOCALE,
            dto.preferredLocales ? dto.preferredLocales : []
        )
    }

    constructor(public toolLocale: ToolLocale, public preferredLocales: string[]) {
    }

    toDto(): UserPreferencesDto {
        return {
            toolLocale: this.toolLocale.toDto(),
            preferredLocales: this.preferredLocales
        }
    }
}
