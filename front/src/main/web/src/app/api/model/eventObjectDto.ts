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
 * Application event notifying that an object changed
 */
export interface EventObjectDto { 
    /**
     * Type of event (help to indicate the payload type)
     */
    type: EventObjectDto.TypeDtoEnum;
    /**
     * Payload of this event.
     */
    payload: any;
}
export namespace EventObjectDto {
    export type TypeDtoEnum = 'CONNECTED_USER_SESSION' | 'ADDED_TRANSLATION_LOCALE' | 'UPDATED_TRANSLATION_LOCALE' | 'DELETED_TRANSLATION_LOCALE' | 'DISCONNECTED_USER_SESSION' | 'ADDED_WORKSPACE' | 'UPDATED_WORKSPACE' | 'DELETED_WORKSPACE' | 'UPDATED_TRANSLATIONS' | 'ADDED_REPOSITORY' | 'UPDATED_REPOSITORY' | 'DELETED_REPOSITORY' | 'UPDATED_USER' | 'DELETED_USER' | 'UPDATED_CURRENT_USER' | 'UPDATED_AUTHENTICATED_USER' | 'DELETED_AUTHENTICATED_USER' | 'UPDATED_CURRENT_AUTHENTICATED_USER' | 'UPDATED_USER_PREFERENCES';
    export const TypeDtoEnum = {
        CONNECTEDUSERSESSION: 'CONNECTED_USER_SESSION' as TypeDtoEnum,
        ADDEDTRANSLATIONLOCALE: 'ADDED_TRANSLATION_LOCALE' as TypeDtoEnum,
        UPDATEDTRANSLATIONLOCALE: 'UPDATED_TRANSLATION_LOCALE' as TypeDtoEnum,
        DELETEDTRANSLATIONLOCALE: 'DELETED_TRANSLATION_LOCALE' as TypeDtoEnum,
        DISCONNECTEDUSERSESSION: 'DISCONNECTED_USER_SESSION' as TypeDtoEnum,
        ADDEDWORKSPACE: 'ADDED_WORKSPACE' as TypeDtoEnum,
        UPDATEDWORKSPACE: 'UPDATED_WORKSPACE' as TypeDtoEnum,
        DELETEDWORKSPACE: 'DELETED_WORKSPACE' as TypeDtoEnum,
        UPDATEDTRANSLATIONS: 'UPDATED_TRANSLATIONS' as TypeDtoEnum,
        ADDEDREPOSITORY: 'ADDED_REPOSITORY' as TypeDtoEnum,
        UPDATEDREPOSITORY: 'UPDATED_REPOSITORY' as TypeDtoEnum,
        DELETEDREPOSITORY: 'DELETED_REPOSITORY' as TypeDtoEnum,
        UPDATEDUSER: 'UPDATED_USER' as TypeDtoEnum,
        DELETEDUSER: 'DELETED_USER' as TypeDtoEnum,
        UPDATEDCURRENTUSER: 'UPDATED_CURRENT_USER' as TypeDtoEnum,
        UPDATEDAUTHENTICATEDUSER: 'UPDATED_AUTHENTICATED_USER' as TypeDtoEnum,
        DELETEDAUTHENTICATEDUSER: 'DELETED_AUTHENTICATED_USER' as TypeDtoEnum,
        UPDATEDCURRENTAUTHENTICATEDUSER: 'UPDATED_CURRENT_AUTHENTICATED_USER' as TypeDtoEnum,
        UPDATEDUSERPREFERENCES: 'UPDATED_USER_PREFERENCES' as TypeDtoEnum
    };
}