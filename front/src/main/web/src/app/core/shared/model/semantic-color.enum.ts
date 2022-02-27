import {ThemePalette} from "@angular/material/core";

export enum SemanticColor {
    PRIMARY = 'primary',

    WARN = 'warn',

    ACCENT = 'accent',
}

export const SEMANTIC_COLOR_THEME = {
    [SemanticColor.PRIMARY]: 'primary' as ThemePalette,
    [SemanticColor.WARN]: 'warn' as ThemePalette,
    [SemanticColor.ACCENT]: 'accent' as ThemePalette,
}