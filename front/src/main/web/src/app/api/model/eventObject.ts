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
export interface EventObject { 
    /**
     * Type of event (help to indicate the payload type)
     */
    type: EventObject.TypeEnum;
    /**
     * Payload of this event.
     */
    payload: any;
}
export namespace EventObject {
    export type TypeEnum = 'CONNECTED_USER_SESSION' | 'ADDED_TRANSLATION_LOCALE' | 'UPDATED_TRANSLATION_LOCALE' | 'DELETED_TRANSLATION_LOCALE' | 'DISCONNECTED_USER_SESSION' | 'ADDED_WORKSPACE' | 'UPDATED_WORKSPACE' | 'DELETED_WORKSPACE' | 'UPDATED_TRANSLATIONS' | 'ADDED_REPOSITORY' | 'UPDATED_REPOSITORY' | 'DELETED_REPOSITORY' | 'UPDATED_USER' | 'DELETED_USER' | 'UPDATED_CURRENT_USER' | 'UPDATED_AUTHENTICATED_USER' | 'DELETED_AUTHENTICATED_USER' | 'UPDATED_CURRENT_AUTHENTICATED_USER' | 'UPDATED_USER_PREFERENCES';
    export const TypeEnum = {
        CONNECTEDUSERSESSION: 'CONNECTED_USER_SESSION' as TypeEnum,
        ADDEDTRANSLATIONLOCALE: 'ADDED_TRANSLATION_LOCALE' as TypeEnum,
        UPDATEDTRANSLATIONLOCALE: 'UPDATED_TRANSLATION_LOCALE' as TypeEnum,
        DELETEDTRANSLATIONLOCALE: 'DELETED_TRANSLATION_LOCALE' as TypeEnum,
        DISCONNECTEDUSERSESSION: 'DISCONNECTED_USER_SESSION' as TypeEnum,
        ADDEDWORKSPACE: 'ADDED_WORKSPACE' as TypeEnum,
        UPDATEDWORKSPACE: 'UPDATED_WORKSPACE' as TypeEnum,
        DELETEDWORKSPACE: 'DELETED_WORKSPACE' as TypeEnum,
        UPDATEDTRANSLATIONS: 'UPDATED_TRANSLATIONS' as TypeEnum,
        ADDEDREPOSITORY: 'ADDED_REPOSITORY' as TypeEnum,
        UPDATEDREPOSITORY: 'UPDATED_REPOSITORY' as TypeEnum,
        DELETEDREPOSITORY: 'DELETED_REPOSITORY' as TypeEnum,
        UPDATEDUSER: 'UPDATED_USER' as TypeEnum,
        DELETEDUSER: 'DELETED_USER' as TypeEnum,
        UPDATEDCURRENTUSER: 'UPDATED_CURRENT_USER' as TypeEnum,
        UPDATEDAUTHENTICATEDUSER: 'UPDATED_AUTHENTICATED_USER' as TypeEnum,
        DELETEDAUTHENTICATEDUSER: 'DELETED_AUTHENTICATED_USER' as TypeEnum,
        UPDATEDCURRENTAUTHENTICATEDUSER: 'UPDATED_CURRENT_AUTHENTICATED_USER' as TypeEnum,
        UPDATEDUSERPREFERENCES: 'UPDATED_USER_PREFERENCES' as TypeEnum
    };
}