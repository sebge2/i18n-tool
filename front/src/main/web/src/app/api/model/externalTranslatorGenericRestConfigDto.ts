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
import { ExternalTranslatorConfigDto } from './externalTranslatorConfigDto';

/**
 * External (not from this tool) source of translation. The source is invoked by a REST call.
 */
export interface ExternalTranslatorGenericRestConfigDto extends ExternalTranslatorConfigDto { 
    /**
     * HTTP method used to call the translation API.
     */
    method: ExternalTranslatorGenericRestConfigDto.MethodDtoEnum;
    /**
     * URL of the translation API.
     */
    url: string;
    /**
     * HTTP query parameters used to call the translation API.
     */
    queryParameters: { [key: string]: string; };
    /**
     * HTTP header parameters used to call the translation API.
     */
    queryHeaders: { [key: string]: string; };
    /**
     * JSON body template used to call the translation API.
     */
    bodyTemplate?: string;
    /**
     * JSON path extracting translations.
     */
    queryExtractor: string;
}
export namespace ExternalTranslatorGenericRestConfigDto {
    export type MethodDtoEnum = 'GET' | 'HEAD' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' | 'OPTIONS' | 'TRACE';
    export const MethodDtoEnum = {
        GET: 'GET' as MethodDtoEnum,
        HEAD: 'HEAD' as MethodDtoEnum,
        POST: 'POST' as MethodDtoEnum,
        PUT: 'PUT' as MethodDtoEnum,
        PATCH: 'PATCH' as MethodDtoEnum,
        DELETE: 'DELETE' as MethodDtoEnum,
        OPTIONS: 'OPTIONS' as MethodDtoEnum,
        TRACE: 'TRACE' as MethodDtoEnum
    };
}