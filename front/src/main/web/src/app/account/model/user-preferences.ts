import {UserPreferencesDto} from "../../api";
import {ALL_LOCALES, ToolLocale} from "../../core/translation/model/tool-locale.model";

export class UserPreferences {

    public static fromDto(dto: UserPreferencesDto): UserPreferences {
        return new UserPreferences(
            ALL_LOCALES.find(toolLocale => toolLocale.toDto() === dto.toolLocale),
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
