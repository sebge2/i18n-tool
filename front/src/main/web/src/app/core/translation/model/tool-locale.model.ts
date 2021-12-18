import { Locale } from './locale.model';
import { UserPreferencesDto } from '../../../api';
import ToolLocaleDtoEnum = UserPreferencesDto.ToolLocaleDtoEnum;

export class ToolLocale {
  constructor(
    public language: string,
    public icon: string,
    public displayName: string,
    private dtoEnum: UserPreferencesDto.ToolLocaleDtoEnum
  ) {}

  public toLocale(): Locale {
    return new Locale(this.language);
  }

  public toString(): string {
    return this.toLocale().toString();
  }

  public toDto(): ToolLocaleDtoEnum {
    return this.dtoEnum;
  }
}

export const EN_LOCALE = new ToolLocale('en', 'flag-icon-gb', 'English', 'ENGLISH');
export const FR_LOCALE = new ToolLocale('fr', 'flag-icon-fr', 'Français', 'FRENCH');
export const DEFAULT_LOCALE = EN_LOCALE;

export const ALL_LOCALES = [EN_LOCALE, FR_LOCALE];
