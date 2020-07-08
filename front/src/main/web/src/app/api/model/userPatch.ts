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
 * The update of a user.
 */
export interface UserPatch { 
    /**
     * The new username.
     */
    username?: string;
    /**
     * The new email address.
     */
    email?: string;
    /**
     * The new password.
     */
    password?: string;
    /**
     * The new avatar URL.
     */
    avatarUrl?: string;
    /**
     * The roles.
     */
    roles?: Array<UserPatch.RolesEnum>;
}
export namespace UserPatch {
    export type RolesEnum = 'MEMBER_OF_ORGANIZATION' | 'MEMBER_OF_REPOSITORY' | 'ADMIN';
    export const RolesEnum = {
        MEMBEROFORGANIZATION: 'MEMBER_OF_ORGANIZATION' as RolesEnum,
        MEMBEROFREPOSITORY: 'MEMBER_OF_REPOSITORY' as RolesEnum,
        ADMIN: 'ADMIN' as RolesEnum
    };
}