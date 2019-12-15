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
 * File composing a bundle file
 */
export interface BundleFileEntryDto { 
    /**
     * Unique identifier of a bundle file entry.
     */
    id: string;
    /**
     * The unique id of the locale associated to this file
     */
    locale: string;
    /**
     * The file part of the bundle file.
     */
    file: string;
}