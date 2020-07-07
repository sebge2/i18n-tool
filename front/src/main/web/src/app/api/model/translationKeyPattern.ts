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
 * Pattern of a translation key to search for.
 */
export interface TranslationKeyPattern { 
    strategy?: TranslationKeyPattern.StrategyEnum;
    pattern?: string;
}
export namespace TranslationKeyPattern {
    export type StrategyEnum = 'EQUAL' | 'STARTS_WITH' | 'ENDS_WITH' | 'CONTAINS';
    export const StrategyEnum = {
        EQUAL: 'EQUAL' as StrategyEnum,
        STARTSWITH: 'STARTS_WITH' as StrategyEnum,
        ENDSWITH: 'ENDS_WITH' as StrategyEnum,
        CONTAINS: 'CONTAINS' as StrategyEnum
    };
}