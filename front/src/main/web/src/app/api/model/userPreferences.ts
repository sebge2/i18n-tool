/**
 * i18n Tool
 * Web API of the i18n tool
 *
 * OpenAPI spec version: 1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

/**
 * Preferences of a user.
 */
export interface UserPreferences { 
    /**
     * The locale to use for the user.
     */
    toolLocale?: UserPreferences.ToolLocaleEnum;
}
export namespace UserPreferences {
    export type ToolLocaleEnum = 'ENGLISH' | 'FRENCH';
    export const ToolLocaleEnum = {
        ENGLISH: 'ENGLISH' as ToolLocaleEnum,
        FRENCH: 'FRENCH' as ToolLocaleEnum
    };
}